package org.opendevup.dao;

import java.util.Date;
import java.util.List;

import org.opendevup.entities.Etudiant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
	
	public List<Etudiant> findByNom(String name);
	public Page<Etudiant> findByNom(String name, Pageable pageable);
	@Query("SELECT e FROM Etudiant e WHERE e.nom LIKE :x")
	public Page<Etudiant> chercherEtudiants(@Param("x")String keyword, Pageable pageable);
	@Query("SELECT e FROM Etudiant e WHERE e.dateNaissance > :x and e.dateNaissance < :y")
	public List<Etudiant> chercherEtudiants(@Param("x")Date d1, @Param("y")Date d2);
}
