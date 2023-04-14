package lithium.service.user.services;

import java.util.Date;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.client.objects.UserFavourites;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.repositories.UserFavouritesRepository;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * {@code UserFavouritesService} This class contains the business logic of the user favourites functionality
 */
@Slf4j
@Service
public class UserFavouritesService {

  private UserFavouritesRepository userFavouritesRepository;
  private UserService userService;

  @Autowired
  public UserFavouritesService(UserService userService, UserFavouritesRepository userFavouritesRepository) {
    this.userFavouritesRepository = userFavouritesRepository;
    this.userService = userService;
  }

  public Response<UserFavourites> createUpdateUserFavourites(UserFavourites userFavourites, LithiumTokenUtil lithiumToken) {
    UserFavourites userSavedFavourites = UserFavourites.builder().build();
    try {
      UserFavourites buildUserFavourites = UserFavourites.builder()
          .competitions(userFavourites.getCompetitions())
          .events(userFavourites.getEvents())
          .build();
      User fromGuid = userService.findFromGuid(lithiumToken.getJwtUser().getGuid());
      userSavedFavourites = findOrCreateUserFavourites(fromGuid, buildUserFavourites);
    } catch (Exception ex) {
      log.error("Failed to create user favourites  for user : " + lithiumToken.getJwtUser().getGuid() + " with exception : " + ex);
      return Response.<UserFavourites>builder().status(Status.INTERNAL_SERVER_ERROR).data(userSavedFavourites).build();
    }
    return Response.<UserFavourites>builder().status(Status.OK_SUCCESS).data(userSavedFavourites).build();
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
  public UserFavourites findOrCreateUserFavourites(User user, UserFavourites userFavourites) {

    lithium.service.user.data.entities.UserFavourites favourites = lithium.service.user.data.entities.UserFavourites.builder()
        .id(!ObjectUtils.isEmpty(user.getUserFavourites()) && user.getUserFavourites().getId() != null ? user.getUserFavourites().getId() : null)
        .events(!StringUtil.isEmpty(userFavourites.getEvents()) ? userFavourites.getEvents() : null)
        .competitions(!StringUtil.isEmpty(userFavourites.getCompetitions()) ? userFavourites.getCompetitions() : null)
        .lastUpdated(new Date())
        .build();
    lithium.service.user.data.entities.UserFavourites newFavourites = userFavouritesRepository.save(favourites);

    if(ObjectUtils.isEmpty(user.getUserFavourites())) {
      user.setUserFavourites(newFavourites);
      userService.save(user);
    }
    return UserFavourites.builder()
        .competitions(newFavourites.getCompetitions())
        .events(newFavourites.getEvents())
        .build();
  }

  public Response<UserFavourites> findUserFavourites(LithiumTokenUtil tokenUtil) {
    UserFavourites currentUserFavourites = UserFavourites.builder().build();
    User fromGuid = userService.findFromGuid(tokenUtil.getJwtUser().getGuid());
    if(!ObjectUtils.isEmpty(fromGuid)) {
      currentUserFavourites = UserFavourites.builder()
          .events(!ObjectUtils.isEmpty(fromGuid.getUserFavourites()) && !ObjectUtils.isEmpty(fromGuid.getUserFavourites().getEvents()) ? fromGuid.getUserFavourites().getEvents() : null)
          .competitions(!ObjectUtils.isEmpty(fromGuid.getUserFavourites()) && !ObjectUtils.isEmpty(fromGuid.getUserFavourites().getCompetitions()) ? fromGuid.getUserFavourites().getCompetitions() : null)
          .build();
    }
    return Response.<UserFavourites>builder().status(Status.OK_SUCCESS).data(currentUserFavourites).build();
  }
}
