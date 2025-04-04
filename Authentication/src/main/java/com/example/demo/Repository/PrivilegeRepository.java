package com.example.demo.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.demo.Model.Privilege;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    @Query("SELECT p FROM Privilege p WHERE p.minorAdmin.id = :minorAdminId")
    Optional<Privilege> findByMinorAdminId(@Param("minorAdminId") Long minorAdminId);
}



	

	

