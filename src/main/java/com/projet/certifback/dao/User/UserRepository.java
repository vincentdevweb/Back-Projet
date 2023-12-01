package com.projet.certifback.dao.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User,Long>  {
    User findByPseudo(String pseudo);
    
}
