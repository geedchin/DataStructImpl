package com.geedchin.NewStack;

/**
 * Created by geedchin on 2017/7/18.
 */
// a true stack
public interface InterfaceStackOnly<E> {
    
    
    public E push(E item);
    
    public E pop();
    
    public E peek();
    
    public boolean empty();
    
    public int size();
    
}
