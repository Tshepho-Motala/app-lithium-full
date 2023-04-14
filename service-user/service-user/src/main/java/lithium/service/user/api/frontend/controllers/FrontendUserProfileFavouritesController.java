package lithium.service.user.api.frontend.controllers;

import lithium.service.Response;
import lithium.service.user.client.objects.UserFavourites;
import lithium.service.user.services.UserFavouritesService;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@code FrontendUserProfileFavouritesController} This class provides implementation of the user favourites functionality
 */
@RestController
@RequestMapping("frontend/profile/favourites")
public class FrontendUserProfileFavouritesController {

  private UserFavouritesService userFavouritesService;

  public FrontendUserProfileFavouritesController(UserFavouritesService userFavouritesService) {
    this.userFavouritesService = userFavouritesService;
  }

  /**
   * This API is used to create or update user's favourites from any linked user profiles for example When a favourite has been added from a betting
   * account it will also be reflected when logged into LSM account, and vice versa
   * @return Response<UserFavourites>
   */
  @PostMapping
  public Response<UserFavourites> createUpdateUserFavourites(@RequestBody UserFavourites userFavourites, LithiumTokenUtil lithiumToken) {
    return userFavouritesService.createUpdateUserFavourites(userFavourites, lithiumToken);
  }

  /**
   * This API is used to retrieve player's favourites from any linked ecosystem user profiles
   * @return Response<UserFavourites>
   */
  @GetMapping
  public Response<UserFavourites> getUserFavourites(LithiumTokenUtil tokenUtil) {
    return userFavouritesService.findUserFavourites(tokenUtil);
  }

}
