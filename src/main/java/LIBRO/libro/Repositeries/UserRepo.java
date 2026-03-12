package LIBRO.libro.Repositeries;


import LIBRO.libro.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo  extends JpaRepository<User,Long> {

    User findByEmail(String email);


}
