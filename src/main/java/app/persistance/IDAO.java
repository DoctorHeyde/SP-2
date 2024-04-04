package app.persistance;

import java.util.List;

public interface IDAO<T, ID> {
    public T create(T t);
    public List<T> getAll();
    public T getById(ID i);
    public T update(T t);
    public void delete(T t);
}
