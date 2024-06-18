package com.metaway.birt.services;

import com.metaway.birt.dtos.PaisDTO;
import com.metaway.birt.repositories.PaisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaisService {

    @Autowired
    private PaisRepository repository;

    @Transactional(readOnly = true)
    public List<PaisDTO> findAll() {
        return repository.findAll().stream().map(PaisDTO::new).toList();
    }
}
