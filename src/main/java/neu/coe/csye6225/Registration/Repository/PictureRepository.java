package neu.coe.csye6225.Registration.Repository;

import neu.coe.csye6225.Registration.Entity.Picture;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface PictureRepository extends CrudRepository<Picture, String> {
}
