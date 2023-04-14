package lithium.service.cdn.cms;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class ServiceCdnCmsModule extends ModuleInfoAdapter {

    public ServiceCdnCmsModule() {
        super();
        roles();
    }

    private void roles() {
        Role.Category gameTileOperations = Role.Category.builder().name("Game Tile Operations").description("These are all the roles relevant to game tile assets.").build();
        addRole(Role.builder().category(gameTileOperations).name("Manage Game Tiles").role("GAME_TILE_MANAGE").description("Manage Game Tiles").build());
        addRole(Role.builder().category(gameTileOperations).name("Game Tile Add").role("GAME_TILE_ADD").description("Add a game tile.").build());
        addRole(Role.builder().category(gameTileOperations).name("Game Tile Delete").role("GAME_TILE_DELETE").description("Remove a game tile").build());
        addRole(Role.builder().category(gameTileOperations).name("Game Tile Publish").role("GAME_TILE_PUBLISH").description("Publish a game tile").build());


        Role.Category bannerOperations = Role.Category.builder().name("Banner Image Operations").description("These are all the roles relevant to banner images assets.").build();
        addRole(Role.builder().category(bannerOperations).name("Manage Banner Images").role("BANNER_IMAGE_MANAGE").description("Manage Banner Images").build());
        addRole(Role.builder().category(bannerOperations).name("Banner Image Add").role("BANNER_IMAGE_ADD").description("Add Banner Image").build());
        addRole(Role.builder().category(bannerOperations).name("Banner Image Delete").role("BANNER_IMAGE_DELETE").description("Remove a banner image").build());
        addRole(Role.builder().category(bannerOperations).name("Banner Image Publish").role("BANNER_IMAGE_PUBLISH").description("Publish a banner image").build());

        Role.Category webAssetsOperations = Role.Category.builder().name("Web Assets Operations").description("These are all the roles relevant to web assets.").build();
        addRole(Role.builder().category(webAssetsOperations).name("Manage Web Assets").role("WEB_ASSET_MANAGE").description("Manage web assets.").build());
        addRole(Role.builder().category(webAssetsOperations).name("Web Asset Add").role("WEB_ASSET_ADD").description("Add a web assets.").build());
        addRole(Role.builder().category(webAssetsOperations).name("Web Asset Delete").role("WEB_ASSET_DELETE").description("Remove a web asset").build());
        addRole(Role.builder().category(webAssetsOperations).name("Web Asset Publish").role("WEB_ASSET_PUBLISH").description("Publish a web asset").build());

    }

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/backoffice/{domainName}/cms-assets/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GAME_TILE_MANAGE', 'BANNER_IMAGE_MANAGE', 'WEB_ASSET_MANAGE')");
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/backoffice/{domainName}/cms-assets/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GAME_TILE_MANAGE','GAME_TILE_ADD','BANNER_IMAGE_MANAGE' ,'BANNER_IMAGE_ADD', 'WEB_ASSET_MANAGE','WEB_ASSET_ADD')");
        http.authorizeRequests().antMatchers("/backoffice/{domainName}/cms-assets/{id}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GAME_TILE_DELETE', 'BANNER_IMAGE_DELETE', 'WEB_ASSET_DELETE')");
        http.authorizeRequests().antMatchers("/backoffice/{domainName}/cms-assets/find-all-by-domain-name-and-type/{type}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName,'GAME_TILE_MANAGE','GAME_TILE_ADD','BANNER_IMAGE_MANAGE' ,'BANNER_IMAGE_ADD', 'WEB_ASSET_MANAGE','WEB_ASSET_ADD')");
    }
}
