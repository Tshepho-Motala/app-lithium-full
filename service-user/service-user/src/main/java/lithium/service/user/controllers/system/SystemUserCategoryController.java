package lithium.service.user.controllers.system;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lithium.service.user.client.UserCategoryClient;
import lithium.service.user.client.objects.UserCategory;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.repositories.UserCategoryRepository;
import lithium.service.user.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/system/user-categories")
public class SystemUserCategoryController implements UserCategoryClient {

  @Autowired
  private UserService userService;
  @Autowired
  private ModelMapper modelMapper;
  @Autowired
  private UserCategoryRepository userCategoryRepository;

  @Override
  @GetMapping
  public List<UserCategory> getUserCategoriesOfUser(@RequestParam(name = "userId") Long userId) {
    List<User> users = new ArrayList<>();
    users.add(userService.findById(userId));
    List<lithium.service.user.client.objects.UserCategory> userCategories = userCategoryRepository.findAllByUsersIn(users).stream()
        .map(uC -> modelMapper.map(uC, lithium.service.user.client.objects.UserCategory.class))
        .collect(Collectors.toList());
    return userCategories;
  }

  @Override
  @GetMapping("/{domainName}")
  public List<UserCategory> getDomainUserCategories(@PathVariable(name="domainName") String domainName) {
    return userCategoryRepository.findAllByDomainName(domainName).stream()
        .map(uC -> modelMapper.map(uC, lithium.service.user.client.objects.UserCategory.class))
        .collect(Collectors.toList());
  }

}
