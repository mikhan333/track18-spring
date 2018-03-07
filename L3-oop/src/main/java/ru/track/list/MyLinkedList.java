package ru.track.list;

import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 * Односвязный список
 */
public class MyLinkedList extends List {

    /**
     * private - используется для сокрытия этого класса от других.
     * Класс доступен только изнутри того, где он объявлен
     * <p>
     * static - позволяет использовать Node без создания экземпляра внешнего класса
     */
    private static class Node {
        Node prev;
        Node next;
        int val;

        Node(Node prev, Node next, int val) {
            this.prev = prev;
            this.next = next;
            this.val = val;
        }
    }

    @Override
    void add(int item) {
        if(_size==0)
        {
            _tail=new Node(null,null,item);
            _head=_tail;
        }
        else
        {

            Node itemNew=new Node(_tail,null,item);
            _tail.next=itemNew;
            _tail=itemNew;
        }
        this._size++;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        int value;
        if(idx>=size())
            throw new NoSuchElementException();
        else
        {
            if (size()==1)
            {
                _head.next=null;
                value=_head.val;
                _size--;
                return value;
            }
            Node del;
            del=_head;
            for (int i=1;i<=idx;i++)
            {
                del=del.next;
            }
            value=del.val;

            if (idx==size()-1)
            {
                Node prev=del.prev;
                prev.next=null;
            }
            else
            {
                Node prev=del.prev;
                Node next=del.next;
                prev.next=next;
                next.prev=prev;
            }

        }
        _size--;
        return value;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        int value;
        if(idx>=size())
            throw new NoSuchElementException();
        else {
            Node del;
            del = _head;
            for (int i = 1; i <= idx; i++) {
                del = del.next;
            }
            value = del.val;
        }
        return value;
    }

    @Override
    int size() {
        return _size;
    }
    Node _tail;
    Node _head;
    int _size;
}
