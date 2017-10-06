import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
public class Controller extends JPanel implements ActionListener,KeyListener,Runnable{
    int marks=0,Speed=0;//marks是得分，Speed是速度
    boolean start = false;
    int rx=0,ry=0;//定义“吃食”的位置坐标
    int eat1=0,eat2=0;
    JDialog dialog = new JDialog();//定义对话框(临时窗口)
    JLabel label = new JLabel("你挂了！你的分数是"+marks+"。");
    JButton jb = new JButton("再来一局");
    JButton jb2 = new JButton("不想玩了");
    Random r = new Random();//产生随机数
    JButton newGame,stopGame;//定义两个按钮
    List<Ground> list = new ArrayList<Ground>();
    int temp=0;
    Thread nThread;
    public Controller() {
        newGame = new JButton("开始");
        stopGame = new JButton("结束");
        /*1.addActionListener添加监听，当鼠标点击这个button的时候会触发监听器
        * 2.后面的this表示继承的接口类(ActionListener)*/
        newGame.addActionListener(this);
        stopGame.addActionListener(this);
        /*1.第一个this表示当前类(继承的JPanel类)的一个实例，通过this可以调用本类所有方法和属性
          2.addKeyListener表示监听键盘，按下、释放或键入键时生成键盘事件
        */
        this.addKeyListener(this);
        /*FlowLayout流布局,有五种布局方式，分别是：
        * FlowLayout.LEFT、FlowLayout.RIGHT、FlowLayout.CENTER、FlowLayout.LEADING(开始边)或FlowLayout.TRAILING(结束边)*/
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.add(newGame);
        this.add(stopGame);
        dialog.setLayout(new GridLayout(2, 1));//GridLayout(int rows, int cols),创建具有指定行数和列数的网格布局
        //在临时对话框上添加标签和按钮
        dialog.add(label);
        dialog.add(jb);

        dialog.setSize(200, 200);
        dialog.setLocation(200, 200);
        dialog.setVisible(false);
        jb.addActionListener(this);
    }
    public void paintComponent(Graphics g)//Graphics类提供基本绘图方法
    {
        super.paintComponent(g);//super.paintComponent(g)是父类JPanel里的方法,会把整个面板用背景色重画一遍,起到清屏的作用
        g.drawRect(10, 40, 400, 300);//drawRect绘制矩形
        g.drawString("分数："+marks, 150, 15);//drawString在指定位置绘制指定文本字符串
        g.drawString("速度："+Speed, 150, 35);
        g.setColor(new Color(255, 0, 0));
        if(start){
            g.fillRect(10+rx*10, 40+ry*10, 10, 10);
            for (int i = 0; i < list.size(); i++) {
                g.setColor(new Color(0, 0, 255));
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
            newGame.setEnabled(false);//为true则使button的事件发生，否则不发生
            start = true;
            rx=r.nextInt(40);//产生40以内的随机数
            ry=r.nextInt(30);
            Ground tempAct = new Ground();
            tempAct.setX(20);
            tempAct.setY(15);
            list.add(tempAct);
            this.requestFocus();//光标进入控件中
            nThread = new Thread(this);
            nThread.start();
            repaint();//repaint重绘曲线
        }
        if(e.getSource()==stopGame)//stopGame则退出
        {
            System.exit(0);
        }
        if(e.getSource()==jb)//为jb则重新开局
        {
            list.clear();
            start=false;
            newGame.setEnabled(true);
            dialog.setVisible(false);
            marks=0;
            Speed=0;
            repaint();
        }
    }
    private void eat()//定义“吃食”方法
    {
        if (rx==list.get(0).getX()&&ry==list.get(0).getY())//对“吃食”的判断:两个点的横纵坐标相同
        {
            rx = r.nextInt(40);//当吃下一个之后，重新产生一个随机的点
            ry = r.nextInt(30);
            Ground tempAct = new Ground();
            tempAct.setX(list.get(list.size()-1).getX());
            tempAct.setY(list.get(list.size()-1).getY());
            list.add(tempAct);
            marks = marks+10;//每吃下一个分数会加10分
            eat1++;
            if(eat1-eat2>=10)//当“吃食”数大于4，会增加一个速度
            {
                eat2=eat1;//并将eat1的值赋给eat2
                Speed++;
            }
        }
    }
    public void otherMove()
    {
        Ground tempAct = new Ground();
        for (int i = 0; i < list.size(); i++) {
            if (i==1) {
                list.get(i).setX(list.get(0).getX());
                list.get(i).setY(list.get(0).getY());
            }
            else if(i>1){
                tempAct=list.get(i-1);
                list.set(i-1, list.get(i));
                list.set(i, tempAct);
            }

        }
    }
    public void move(int x,int y){
        if (minYes(x, y)) {
            otherMove();
            list.get(0).setX(list.get(0).getX()+x);
            list.get(0).setY(list.get(0).getY()+y);
            eat();
            repaint();
        }else {
            nThread = null;
            label.setText("你挂了！你的分数是"+marks+"。");
            dialog.setVisible(true);
        }

    }
    public boolean minYes(int x,int y){
        if (!maxYes(list.get(0).getX()+x,list.get(0).getY()+ y)) {
            return false;
        }
        return true;
    }
    public boolean maxYes(int x,int y){
        if (x<0||x>=40||y<0||y>=30) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            if (i>1&&list.get(0).getX()==list.get(i).getX()&&list.get(0).getY()==list.get(i).getY()) {
                return false;
            }
        }
        return true;
    }
    public void keyPressed(KeyEvent e) {
        if(start){
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    move(0, -1);
                    temp=1;
                    break;
                case KeyEvent.VK_DOWN:
                    move(0, 1);
                    temp=2;
                    break;
                case KeyEvent.VK_LEFT:
                    move(-1, 0);
                    temp=3;
                    break;
                case KeyEvent.VK_RIGHT:
                    move(1, 0);
                    temp=4;
                    break;

                default:
                    break;
            }
        }
    }
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
    public void run() {
        while (start) {
            switch (temp) {
                case 1:
                    move(0, -1);
                    break;
                case 2:
                    move(0, 1);
                    break;
                case 3:
                    move(-1, 0);
                    break;
                case 4:
                    move(1, 0);
                    break;
                default:
                    break;
            }
            repaint();
            try {
                Thread.sleep(300-30*Speed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
