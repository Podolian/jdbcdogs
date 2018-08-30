package com.demians.dao;

import com.demians.model.Breed;
import com.demians.model.Vocation;

import java.util.List;

public interface DogsDao {
    void save(Breed breed);

    List<Breed> findAll();

    void update(Breed breed);

    void remove(Breed breed);

    List<Breed> breedSuitableFor(Vocation vocation);
}
