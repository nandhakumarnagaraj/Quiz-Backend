package com.nirmaan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nirmaan.entity.Option;

@Repository
public interface OptionRepository extends JpaRepository<Option, Integer> {

}
