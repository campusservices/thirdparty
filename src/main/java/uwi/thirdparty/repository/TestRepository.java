package uwi.thirdparty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uwi.thirdparty.entity.Test;

@Repository
public interface TestRepository extends JpaRepository<Test, Long>  {

}
