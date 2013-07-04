package be.philipluyckx.geocaching.database;


import java.util.Iterator;

/**
 * Created by Philip on 1/07/13.
 */
public class SortedKeyedList<K extends Comparable<K>, V> implements Iterable<V> {
  private Entry<K, V> mList[];
  private int mSize;

  public SortedKeyedList() {
    mList = new Entry[10];
  }

  public V get(int i) {
    if (i >= 0 && i < mSize) {
      return mList[i].mValue;
    } else {
      return null;
    }
  }

  public V get(K key) {
    if (mSize == 0) {
      return null;
    } else {
      int index = this.find(key, 0, mSize - 1);
      return get(index);
    }
  }

  public K getKey(int index) {
    return mList[index].mKey;
  }

  public int size() {
    return mSize;
  }

  public int capacity() {
    return mList.length;
  }

  public boolean shrink() {
    if (mSize < mList.length) {
      Entry<K, V> newList[] = new Entry[mSize];
      for (int i = 0; i < mSize; i++) {
        newList[i] = mList[i];
      }

      mList = newList;
      return true;
    } else {
      return true;
    }
  }

  public boolean add(K key, V value) {
    if (checkCapacity()) {
      int index = mSize - 1;
      Entry<K, V> entry = new Entry<K, V>(key, value);

      while (index >= 0 && mList[index].compareTo(entry) > 0) {
        mList[index + 1] = mList[index];
        index--;
      }

      mList[index + 1] = entry;
      mSize++;

      return true;
    } else {
      return false;
    }
  }

  public boolean remove(int index) {
    return remove(index, false);
  }

  public boolean remove(K key) {
    return remove(find(key, 0, mSize - 1));
  }

  public boolean remove(int index, boolean setNull) {
    if (index >= 0 && index < mSize) {
      for (int i = index; i < mSize - 1; i++) {
        mList[i] = mList[i + 1];
      }
      mList[mSize - 1] = null;
      mSize--;

      return true;
    } else {
      return false;
    }
  }

  public boolean removeAll() {
    return removeAll(false);
  }

  public boolean removeAll(boolean nullAll) {
    if (nullAll) {
      for (int i = 0; i < mSize; i++) {
        mList[i] = null;
      }
    }

    mSize = 0;

    return true;
  }

  private void swap(int index1, int index2) {
    Entry<K, V> tmp = mList[index1];
    mList[index1] = mList[index2];
    mList[index2] = tmp;
  }

  @Override
  public Iterator<V> iterator() {
    return new SortedKeyListIterator();
  }

  private boolean checkCapacity() {
    return checkCapacity(1);
  }

  private boolean checkCapacity(int extra) {
    int total = mSize + extra;
    if (mSize + extra > mList.length) {
      return ensureCapacity(total + total >> 2);
    } else {
      return true;
    }
  }

  public boolean ensureCapacity(int capacity) {
    if (capacity < mList.length) {
      return true;
    }

    Entry<K, V> newList[] = new Entry[capacity];
    for (int i = 0; i < mSize; i++) {
      newList[i] = mList[i];
    }

    mList = newList;
    return true;
  }

  private int find(K key, int low, int high) {
    int center = (low + high) / 2;
    int compare = key.compareTo(mList[center].mKey);
    if (compare == 0) {
      return center;
    } else if (compare < 0 && center > low) {
      return find(key, low, center - 1);
    } else if (compare > 0 && center < high) {
      return find(key, center + 1, high);
    } else {
      return -1;
    }
  }

  private static class Entry<K extends Comparable<K>, V> implements Comparable<Entry<K, V>> {
    private K mKey;
    private V mValue;

    public Entry(K key, V value) {
      mKey = key;
      mValue = value;
    }

    @Override
    public int compareTo(Entry<K, V> kvEntry) {
      return mKey.compareTo(kvEntry.mKey);
    }
  }

  private class SortedKeyListIterator implements Iterator<V> {
    private int mIndex;

    public SortedKeyListIterator() {
      this.mIndex = -1;
    }

    @Override
    public boolean hasNext() {
      return (mIndex + 1 < mSize);
    }

    @Override
    public V next() {
      return mList[++mIndex].mValue;
    }

    @Override
    public void remove() {
      SortedKeyedList.this.remove(mIndex);
      mIndex--;
    }
  }
}
