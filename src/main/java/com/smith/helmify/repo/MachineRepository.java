package com.smith.helmify.repo;

import com.smith.helmify.model.meta.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Integer> {
//    Optional<Machine> findById(Integer id);
}
