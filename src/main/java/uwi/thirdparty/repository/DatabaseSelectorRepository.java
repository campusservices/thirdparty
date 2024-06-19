package uwi.thirdparty.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import uwi.thirdparty.entity.Databaseselector;


@Repository
public interface DatabaseSelectorRepository extends JpaRepository<Databaseselector, Long> {
	
	@Transactional
	@Modifying
	@Query("UPDATE Databaseselector a SET a.name =?1 WHERE a.id =?2")
	void updateSelectorDatabase(String name, int id);
}
