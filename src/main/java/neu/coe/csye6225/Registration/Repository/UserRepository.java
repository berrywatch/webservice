package neu.coe.csye6225.Registration.Repository;

import neu.coe.csye6225.Registration.Entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

// support for CRUD operations
@Component
public interface UserRepository extends CrudRepository<User, String> {

}