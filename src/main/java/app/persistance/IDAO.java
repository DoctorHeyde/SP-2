package app.persistance;

import java.util.List;

public interface IDAO<T, DTO, ID> {
    public T create(T t);
    public List<DTO> getAll();
    public T getByID(ID i);
    public void update(T t);
    public void delete(T t);
}
