package com.metaway.birt.services;

import com.metaway.birt.dtos.UfDTO;
import com.metaway.birt.repositories.UfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UfService {

    @Autowired
    private UfRepository repository;

    @Transactional(readOnly = true)
    public List<UfDTO> findAll() {
        return repository.findAll().stream().map(UfDTO::new).toList();
    }
}
