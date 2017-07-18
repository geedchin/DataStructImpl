package com.geedchin.NewStack;

import java.util.Stack;

/**
 * Created by geedchin on 2017/7/18.
 * _
 * 该类仅支持严格意义上的栈操作，不支持指定编号删除及指定位置增加。
 * _
 * 数据结构（默认大小）：二维数组（第一维根据栈数目动态扩展，第二维初始化默认长度32）
 * [x][x.len]   x为第二维度下标，x.len为 elementArray[x] 的长度
 * 总计Stack的最大长度
 * [0][16]                 16
 * [1][16]                 32
 * [2][32]                 64
 * ...
 * [k][16<<(k-1)]          16<<k
 * // 其中len 最大为0x7FFFFFFF/2+1   即 有符号最大整数的一半+1
 * // 当len取最大值时，elementArray[k][maxLen-1](即数组最后一位)永远不存数据，这是为了保证elementCount不溢出
 * _
 * 分析：
 * _    1、同java.util.Stack 相比，当栈的长度不够时无需重新创建数组，copy数组
 * _    2、无需copy数组：copy数组有个问题当扩展时，原数组保留，开辟更大数组（此时可能会OOM），然后将原数组copy到新数组，释放原数组
 * _    3、强制严格按照栈定义来操作栈带来的好处：功能完整且单一，避免了使用时对某些函数混淆或者其他
 * _
 * _    4、强制严格按照栈定义来操作栈带来的坏处：只能栈尾删除，栈尾增加
 * _    5、劣势2：获取对象引用需定位两次（elementArray[index1][index2]），a、定位到index1 获取引用；b、然后根据index2和之前的引用定位获得对象地址
 * 
 * 时间分析：(main函数里执行  so.TestStack  so.TestThis，测试两次每次只运行一个函数，防止缓存数据造成后运行的函数时间受影响)
 * _
 * _    java.util.Stack         16.26s
 * _    com.geedchin.NewStack   12.456s
 */
public class StackOnly<E> implements InterfaceStackOnly<E> {
    
    public StackOnly() {
        
    }
    
    public static void main(String[] args) throws Exception {
        
        StackOnly so = new StackOnly();
        
        
//        so.TestStack();
        so.TestThis();
        
        
    }
    
    public void TestStack() throws Exception {
        
        int i = 0;
        
        long start;
        int count;
        
        Stack stack = new Stack();
        count = 2;
        start = System.currentTimeMillis();
        while (count-- >= 0) {
            
            for (i = 0; i < 20000000; i++) {
                stack.push(Integer.valueOf(i));
            }
            while (stack.size() > 0) {
                Integer e = (Integer) stack.pop();
            }
        }
        System.out.println((System.currentTimeMillis() - start) / 1000.0);
    }
    public void TestThis() throws Exception {
        
        int i = 0;
        
        long start;
        int count;

        count = 2;
        start = System.currentTimeMillis();
        while (count-- >= 0) {
            
            for (i = 0; i < 20000000; i++) {
                push((E) Integer.valueOf(i));
                //System.out.println(i);
            }
            while (this.size() > 0) {
                E e = pop();
                //System.out.println(e + "\t" + this.size() + "\t" + index1 + "\t" + index2);
            }
        }
        System.out.println((System.currentTimeMillis() - start) / 1000.0);
    }
    
    @Override
    public synchronized E push(E item) {
        
        if (elementCount == MAXSIZE) {
            throw new IllegalArgumentException("Out of capacity");
        }
        elementCount++;
        if (checkNeedNewArrayIfAdd()) {
            index1 = elementCount == 1 ? 0 : index1 + 1;
            index2 = 0;
            elementArray[index1] = new Object[initYsize(index1)];
        } else {
            index2++;
        }
        elementArray[index1][index2] = item;
        return null;
    }
    
    @Override
    public synchronized E pop() {
        
        if (elementCount == 0) {
            throw new IllegalArgumentException("The Stack do not have any element");
        }
        E item = (E) elementArray[index1][index2];
        elementArray[index1][index2] = null;
        if (index2 == 0) {
            // if index1 == 0 then there is nothing,so index2 = 0;
            // else if index1 == 1 then basesize-1 
            // else basesize*2^(index1-2) - 1
            index2 = index1 == 0 ? 0 : (index1 == 1 ? BASESIZE - 1 : ((BASESIZE << (index1 - 2)) - 1));
            index1 = index1 == 0 ? 0 : index1 - 1;
        } else {
            index2--;
        }
        elementCount--;
        return item;
    }
    
    @Override
    public synchronized E peek() {
        if (elementCount == 0) {
            throw new IllegalArgumentException("The Stack do not have any element");
        }
        return (E) elementArray[index1][index2];
    }
    
    @Override
    public synchronized boolean empty() {
        return elementCount == 0;
    }
    
    @Override
    public synchronized int size() {
        return elementCount;
    }
    
    private int initYsize(int index) {
        if (index == 0) return BASESIZE;
        int result = BASESIZE << (index - 1);
        if (result <= 0) {
            throw new IllegalArgumentException("Out of capacity");
        }
        return result;
    }
    
    /**
     * @return
     * @Description check whether need new a array or not when inserting a element;elementCount is update
     */
    private boolean checkNeedNewArrayIfAdd() {
        if (elementCount == 1) return true;
        if ((elementCount == BASESIZE + 1) || ((BASESIZE << index1) + 1 == elementCount)) return true;
        return false;
    }
    
    private final static int MAXSIZE = 0x7FFFFFFF;
    private int index1 = 0;
    private int index2 = 0;
    private int elementCount = 0;
    /**
     * elementArray[0]'s size
     */
    private final static int BASESIZE = 16;
    /**
     * elementArray[][]
     * index:size             defaultSize          defaultTotalCount
     * [0]:[BASESIZE]              16                       16
     * [1]:[BASESIZE]              16                       32
     * [2]:[BASESIZE*2]            32                       64
     * ...
     * [k]:[BASESIZE*2^(k01)]     16*2^(k-1)                16*2^k
     * ...                        MAXSIZE/2+1               MAXSIZE+1
     * the last element of the last array is not be used because that the count may be > MAXSIZE
     */
    private Object elementArray[][] = new Object[32][];
    
    
}
