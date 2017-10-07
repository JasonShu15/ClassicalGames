import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
public class Controller extends JPanel implements ActionListener,KeyListener,Runnable{
    int marks=0,speed=0;
    boolean start = false;
    int rx=0,ry=0;//rx,ry分别代表“吃食”的横纵坐标
    int eat1=0,eat2=0;//根据eat2-eat1的差值来判断是否需要增加速度
    JDialog dialog = new JDialog();//定义对话框(临时窗口)
    JLabel label = new JLabel("游戏结束！你的分数是"+marks+"。");
    JButton jb1 = new JButton("再来一局");
    JButton jb2 = new JButton("不想玩了");
    Random r = new Random();//产生随机数
    JButton newGame,stopGame;
    List<Ground> list = new ArrayList<Ground>();//List指的是集合，List<Ground>就代表这个集合中存放了很多个Ground对象,泛型
    int temp=0;
    Thread nThread;//定义一个线程
    public Controller() {
        newGame = new JButton("开始");
        stopGame = new JButton("结束");
        /*1.addActionListener添加监听，当鼠标点击这个button的时候会触发监听器
        * 2.后面的this表示继承的接口类(ActionListener)的一个实例对象*/
        newGame.addActionListener(this);
        stopGame.addActionListener(this);
        /*1.第一个this表示当前类(继承的JPanel类)的一个实例对象，通过this可以调用本类所有方法和属性
          2.addKeyListener表示监听键盘，按下、释放或键入键时生成键盘事件
        */
        this.addKeyListener(this);
        /*FlowLayout流布局,有五种布局方式，分别是：
        * FlowLayout.LEFT、FlowLayout.RIGHT、FlowLayout.CENTER、FlowLayout.LEADING(开始边)或FlowLayout.TRAILING(结束边)*/
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.add(newGame);
        this.add(stopGame);
        dialog.setLayout(new GridLayout(3, 3));//GridLayout(int rows, int cols),创建具有指定行数和列数的网格布局
        //在临时对话框上添加标签和按钮
        dialog.add(label);
        dialog.add(jb1);
        dialog.add(jb2);
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setSize(200, 200);
        dialog.setLocation(200, 200);
        dialog.setVisible(false);
        jb1.addActionListener(this);
        jb2.addActionListener(this);
    }
    public void paintComponent(Graphics g)//Graphics类提供基本绘图方法
    {
        /*1.super.paintComponent(g)是父类JPanel里的方法,会把整个面板用背景色重画一遍,起到清屏的作用
        * 2.这里画的图就是黑线里面的矩形*/
        super.paintComponent(g);
        g.drawRect(10, 40, 400, 300);//drawRect绘制矩形，(10,40)表示矩形左上角点的坐标
        g.drawString("分数："+marks, 150, 15);//drawString在指定位置绘制指定文本字符串
        g.drawString("速度："+speed, 150, 35);
        g.setColor(new Color(255, 0, 0));
        if(start)
        {
            /*1.fillRect:用预定的颜色填充一个矩形，得到一个着色的矩形块
            * 2.其中参数x和y指定左上角的位置，参数width和height是矩形的宽和高
            * 3.蛇的位置和rx和ry有关,因此其位置是随机的,而吃食的位置和rx及ry无关，是固定的*/
            g.fillRect(10+rx*10, 40+ry*10, 10, 10);
            for (int i = 0; i < list.size(); i++)
            {
                g.setColor(new Color(0, 0, 0));
                g.fillRect(10+list.get(i).getX()*10, 40+list.get(i).getY()*10, 10, 10);
            }
        }
    }
    /*1.ActionEvent动作事件类
    * 2.本方法实现贪吃蛇在布局中的行动*/
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource()==newGame)//getSource，最初发生event的对象
        {
            /*1.为true则使button的事件发生，否则不发生
             *2.这里鼠标点击后,事件并不是立即发生,而是需要有键盘的移动,事件才发生
             */
            newGame.setEnabled(false);
            start = true;
            rx=r.nextInt(40);//产生40以内的随机数
            ry=r.nextInt(30);
            Ground ground= new Ground();
            ground.setX(10);
            ground.setY(10);
            list.add(ground);
            this.requestFocus();//光标进入控件中
            nThread = new Thread(this);
            nThread.start();
            repaint();//repaint重绘曲线
        }
        if(e.getSource()==stopGame)//stopGame则退出
        {
            System.exit(0);
        }
        if(e.getSource()==jb1)//为jb1则重新开局
        {
            list.clear();//移除所有元素
            start=false;
            newGame.setEnabled(true);//重新开局之后,直接移动,不等键盘移动
            dialog.setVisible(false);
            marks=0;
            speed=0;
            repaint();
        }
        if(e.getSource()==jb2)//为jb2则退出
        {
            System.exit(0);
        }
    }
    private void eat()//定义“吃食”方法
    {
        if (rx==list.get(0).getX()&&ry==list.get(0).getY())//对“吃食”的判断:两个点的横纵坐标相同
        {
            rx = r.nextInt(40);//当吃下一个之后，重新产生一个随机的点
            ry = r.nextInt(30);
            Ground ground = new Ground();

            //question1

            ground.setX(list.get(list.size()-1).getX());
            ground.setY(list.get(list.size()-1).getY());
            list.add(ground);

            marks = marks+10;//每吃下一个分数会加10分
            eat1++;
            if(eat1-eat2>=10)//当“吃食”数大于4，会增加一个速度
            {
                eat2=eat1;//并将eat1的值赋给eat2
                speed++;
            }
        }
    }

    public void howToMove()
    {
        Ground ground = new Ground();
        for (int i = 0; i < list.size(); i++)
        {
            if (i==1)//只有一个点(还没有“吃食”)的情况，
            {
                /*1.list.get(0).getX()返回值是第一个点的横坐标
                * 2.当i==1的时候，将第二个点的坐标设为第一个点的坐标
                * 3.执行完之后，i>1,跳入到下一个循环*/
                list.get(i).setX(list.get(0).getX());
                list.get(i).setY(list.get(0).getY());
            }
            else if(i>1)//已经“吃食”
            {
                ground=list.get(i-1);
                /*1.list.set(int index, E element),将list集合中第index个元素被的值换成element
                * 2.已经吃食，就将最后一个的坐标换为吃之前的那一个，其实和前面也是一样，只不过i-1要保证>=0*/
                list.set(i-1, list.get(i));
                list.set(i,  ground);
            }

        }
    }
    /*在贪吃蛇移动的过程中要注意的两点：
    * 1.是否“挂了”,如果挂了就结束游戏
    * 2.如果没有挂,该怎么样变换坐标*/
    public void moving(int x,int y)
    {
        if (gameOn(x, y))//如果游戏在进行中...
        {
            howToMove();//随着游戏的过程变换贪吃蛇的坐标
            list.get(0).setX(list.get(0).getX()+x);//howToMove里面没有强调第一个元素list.get(0)坐标该如何变换
            list.get(0).setY(list.get(0).getY()+y);
            eat();
            repaint();//吃掉之后重绘曲线
        }
        else//如果游戏挂了...
        {
            nThread = null;//停掉之前的线程
            label.setText("分数"+marks);//给出分数
            dialog.setVisible(true);//弹出对话框
        }

    }
    public boolean gameOn(int x,int y)//游戏没有“挂”
    {
        //question2

        if (!gameOver(list.get(0).getX()+x,list.get(0).getY()+ y))//如果游戏不结束
        {
            return false;
        }
        return true;
    }
    public boolean gameOver(int x,int y)//判断“挂了"的几种情况
    {
        if (x<0||x>=40||y<0||y>=30)//撞墙，“挂了”
        {
            return false;
        }
        for (int i = 0; i < list.size(); i++)//这种情况也“挂了”
        {
            //“吃食”后，有任意的两点坐标相同，“挂了”
            if (i>1&&list.get(0).getX()==list.get(i).getX()&&list.get(0).getY()==list.get(i).getY())
            {
                return false;
            }
        }
        return true;
    }
    public void keyPressed(KeyEvent e) {
        if(start)
        {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP://键盘往上走，纵坐标减少，左上角坐标为(0,0)
                    moving(0, -1);
                    temp=1;
                    break;
                case KeyEvent.VK_DOWN:
                    moving(0, 1);
                    temp=2;
                    break;
                case KeyEvent.VK_LEFT:
                    moving(-1, 0);
                    temp=3;
                    break;
                case KeyEvent.VK_RIGHT:
                    moving(1, 0);
                    temp=4;
                    break;

                default:
                    break;
            }
        }
    }
    public void keyReleased(KeyEvent e) //键被弹起,什么事情都不用干
    {

    }
    public void keyTyped(KeyEvent e)//有字节输入,什么事情也不干
    {

    }
    public void run() {
        while (start) {
            switch (temp) {
                case 1:
                    moving(0, -1);
                    break;
                case 2:
                    moving(0, 1);
                    break;
                case 3:
                    moving(-1, 0);
                    break;
                case 4:
                    moving(1, 0);
                    break;
                default:
                    break;
            }
            repaint();
            try {
                Thread.sleep(300-30*speed);//线程休息时间
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
