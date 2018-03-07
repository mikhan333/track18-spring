package ru.track.list;

import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 *
 * Должен иметь 2 конструктора
 * - без аргументов - создает внутренний массив дефолтного размера на ваш выбор
 * - с аргументом - начальный размер массива
 */
public class MyArrayList extends List {

    public MyArrayList() {
        mas=new int[100];
        _size=0;
    }

    public MyArrayList(int capacity) {
        mas=new int[capacity];
        _size=0;
    }

    @Override
    void add(int item) {
        if (_size==mas.length)
        {
            int[] masNew = new int[(_size+1)*2];
            System.arraycopy(mas, 0, masNew, 0, _size);
            masNew[_size]=item;

            //mas=new
            mas = masNew;
        }
        else
        {
            this.mas[_size]=item;

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
            value=mas[idx];

            for (int j=idx;j<size();j++)
            {
                mas[j]=mas[j+1];
            }
        }
        _size--;
        return value;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if(idx>=size())
            throw new NoSuchElementException();
        else {
            return mas[idx];
        }
    }

    @Override
    int size() {
        return _size;
    }

    int _size;
    int[] mas;
}
