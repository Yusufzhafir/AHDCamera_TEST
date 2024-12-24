package com.quectel.multicamera.utils;

public class IntQueueUtils {
    private int maxSize;

    private int[] queArray;

    private int front;

    private int rear;

    private int nItems;

    public IntQueueUtils(int s) {
        maxSize = s;
        queArray = new int[maxSize];
        front = 0;
        rear = -1;
        nItems = 0;
    }

    public void insert(int j) {
//        System.out.println("zyz --> insert --> j --> "+j);
        if (rear == maxSize - 1)
            rear = -1;
        queArray[++rear] = j;
        nItems++;
    }


    public int remove() {
        int temp = queArray[front++];
        if (front == maxSize)
            front = 0;
        nItems--;
        return temp;
    }

    public int peekFront() {
        int unit = queArray[front];
//        System.out.println("zyz --> peekFront --> unit --> "+unit);
        remove();
        return unit;
    }

    //查询
    public boolean inquire(int u){
        for (int i=0; i<nItems; i++){
            if (queArray[front] == u){
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
//        System.out.println("zyz --> isEmpty --> nItems --> "+nItems);
        return (nItems == 0);
    }

    public boolean isFull() {
        return (nItems == maxSize);
    }

    public int size() {
        return nItems;
    }
}
