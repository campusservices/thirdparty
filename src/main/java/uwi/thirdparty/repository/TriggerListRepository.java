package uwi.thirdparty.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import uwi.thirdparty.entity.Oracletrigger;


@Repository
public interface TriggerListRepository extends JpaRepository<Oracletrigger, Long> {

	@Query("SELECT a FROM Oracletrigger a")
	List<Oracletrigger> findAllTriggers();
}
