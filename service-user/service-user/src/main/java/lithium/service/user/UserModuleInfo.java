package lithium.service.user;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class UserModuleInfo extends ModuleInfoAdapter {

    public UserModuleInfo() {
//		addMenuItem(
//			new MenuItem("Administration", "Administration", "/admin/", "gear", "ADMIN,TRANSLATE_ADMIN", 1000).addChild(
//				new MenuItem("Languages", "Languages", "/admin/translations", "globe", "ADMIN,TRANSLATE_ADMIN", 200)
//			)
//		);
//		Category adminCategory = Category.builder().name("Admin Operations").description("Super Network Admin Operations").build();
//		addRole(Role.builder().category(adminCategory).name("Admin User").role("ADMIN").description("Super Network Admin Role").build());

        Category betaCategory = Category.builder()
            .name("BETA Features Access")
            .description("Allow access to features currently in BETA")
            .build();
        addRole(Role.builder()
            .category(betaCategory)
            .name("BETA Features Access")
            .role("BETA")
            .description("Allow access to features currently in BETA")
            .build());

        Category groupCategory = Category.builder().name("Group Operations").description("Managing of groups").build();
        addRole(Role.builder().category(groupCategory).name("Groups List").role("GROUPS_LIST").description("View all available groups").build());
        addRole(Role.builder().category(groupCategory).name("Group View").role("GROUP_VIEW").description("View details about a group").build());
        addRole(Role.builder().category(groupCategory).name("Group Edit").role("GROUP_EDIT").description("Edit group details").build());
        addRole(Role.builder().category(groupCategory).name("Group Create").role("GROUP_ADD").description("Create new groups").build());
        addRole(Role.builder().category(groupCategory).name("Group Delete").role("GROUP_DELETE").description("Delete a group").build());
        addRole(Role.builder().category(groupCategory).name("Group Enable").role("GROUP_ENABLE").description("Enable / Disable Groups").build());
        addRole(Role.builder().category(groupCategory).name("Group Roles View").role("GROUP_ROLES_VIEW").description("View Group Role Details").build());
        addRole(Role.builder().category(groupCategory).name("Group Roles Edit").role("GROUP_ROLES_EDIT").description("Edit Group Role Details").build());
        addRole(Role.builder().category(groupCategory).name("Group Roles Add").role("GROUP_ROLES_ADD").description("Add new roles to this group.").build());
        addRole(Role.builder().category(groupCategory).name("Group Roles Remove").role("GROUP_ROLES_REMOVE").description("Remove roles from this group.").build());
        addRole(Role.builder().category(groupCategory).name("Group Users View").role("GROUP_USERS_VIEW").description("View Group User Details").build());
        addRole(Role.builder().category(groupCategory).name("Group Users Edit").role("GROUP_USERS_EDIT").description("Edit Group User Details").build());
        addRole(Role.builder().category(groupCategory).name("Group Users Add").role("GROUP_USERS_ADD").description("Add new users to this group").build());
        addRole(Role.builder().category(groupCategory).name("Group Users Remove").role("GROUP_USERS_REMOVE").description("Remove users from this group").build());

        Category userCategory = Category.builder().name("User Operations").description("Operations related to users.").build();
        addRole(Role.builder().category(userCategory).name("User View").role("USER_VIEW").description("View User Details").build());
        addRole(Role.builder().category(userCategory).name("User Edit").role("USER_EDIT").description("Edit User Details").build());
        addRole(Role.builder().category(userCategory).name("User Create").role("USER_ADD").description("Create User Details").build());
        addRole(Role.builder().category(userCategory).name("User Delete").role("USER_DELETE").description("Delete User Details").build());
        addRole(Role.builder().category(userCategory).name("User OptOut").role("USER_OPT").description("User OptOut Config").build());

        Category playerCategory = Category.builder().name("Player Operations").description("Operations related to players.").build();
        addRole(Role.builder().category(playerCategory).name("Player View").role("PLAYER_VIEW").description("View Player Details").build());
        addRole(Role.builder().category(playerCategory).name("Player Update").role("PLAYER_EDIT").description("Edit Player Details").build());
        addRole(Role.builder().category(playerCategory).name("Player Create").role("PLAYER_ADD").description("Create Player Details").build());
        addRole(Role.builder().category(playerCategory).name("Player Delete").role("PLAYER_DELETE").description("Obfuscate player details, mark deleted, and opt out of everything.").build());
        addRole(Role.builder().category(playerCategory).name("Player Balance Adjust").role("PLAYER_BALANCE_ADJUST").description("Make balance adjustments for players.").build());
        addRole(Role.builder().category(playerCategory).name("Player Balance Adjust Write-Off").role("PLAYER_BALANCE_ADJUST_WRITE_OFF").description("Make write-off for players.").build());
        addRole(Role.builder().category(playerCategory).name("Player Password Change").role("PLAYER_PASSWD").description("Change player passwords.").build());
        addRole(Role.builder().category(playerCategory).name("Player Status Change").role("PLAYER_STATUS").description("Change player status.").build());
        addRole(Role.builder().category(playerCategory).name("Player Document Add").role("PLAYER_DOC_ADD").description("Add a document for a player.").build());
        addRole(Role.builder().category(playerCategory).name("Player Document Update").role("PLAYER_DOC_EDIT").description("Edit a document for a player.").build());
        addRole(Role.builder().category(playerCategory).name("Player Document - Upload Shortcuts").role("PLAYER_DOC_UPLOAD_SHORTCUTS").description("Predefined Upload Shortcuts.").build());
        addRole(Role.builder().category(playerCategory).name("Player Information Update").role("PLAYER_INFO_EDIT").description("Edit information for a player.").build());
        addRole(Role.builder().category(playerCategory).name("Player DOB Update").role("PLAYER_DOB_EDIT").description("Edit Player Date of Birth").build());
        addRole(Role.builder().category(playerCategory).name("Player Username Edit").role("PLAYER_USERNAME_EDIT").description("Edit Player Username").build());

        addRole(Role.builder().category(playerCategory).name("Player Document External List").role("PLAYER_DOCUMENT_EXTERNAL_LIST").description("See the external document list for a player.").build());
        addRole(Role.builder().category(playerCategory).name("Player Document External Content").role("PLAYER_DOCUMENT_EXTERNAL_CONTENT").description("View the content of an individual external document for a player.").build());
        addRole(Role.builder().category(playerCategory).name("Player Document Internal List").role("PLAYER_DOCUMENT_INTERNAL_LIST").description("See the internal document list for a player.").build());
        addRole(Role.builder().category(playerCategory).name("Player Document Internal Content").role("PLAYER_DOCUMENT_INTERNAL_CONTENT").description("View the content of an individual external document for a player.").build());

        addRole(Role.builder().category(playerCategory).name("Clear Failed Reset Password Counter").role("PLAYER_CLEAR_FAILED_RESET").description("Allow a user to clear the failed password reset attempt.").build());
        addRole(Role.builder().category(playerCategory).name("Player OptOut").role("PLAYER_OPT").description("Player OptOut Config").build());
        addRole(Role.builder().category(playerCategory).name("Player Password Reset Email/SMS Send").role("PLAYER_PASSWORD_RESET").description("Allow agents to reset password on behalf of the player in the backoffice.").build());

        addRole(Role.builder().category(playerCategory).name("Player Tag Change").role("PLAYER_TAG_EDIT").description("Change player tag.").build());
        addRole(Role.builder().category(playerCategory).name("Player Validate Mobile").role("PLAYER_VALIDATE_MOBILE").description("Change player mobile validation status.").build());
        addRole(Role.builder().category(playerCategory).name("Player Validate Email").role("PLAYER_VALIDATE_EMAIL").description("Change player email validation status.").build());
        addRole(Role.builder().category(playerCategory).name("Player Statement of Wealth").role("PLAYER_ALLOW_SOW_DOCUMENT").description("Change player statement of wealth required status.").build());
        addRole(Role.builder().category(playerCategory).name("Player Comment Add").role("PLAYER_COMMENT_ADD").description("Add player comment.").build());
        addRole(Role.builder().category(playerCategory).name("Player Address Edit").role("PLAYER_ADDRESS_EDIT").description("Change player residential/postal address.").build());
        addRole(Role.builder().category(playerCategory).name("Edit Player Place of Birth").role("PLAYER_PLACE_OF_BIRTH_EDIT").description("Edit place of birth for player.").build());
        addRole(Role.builder().category(playerCategory).name("Player Promotional Opt-out Edit").role("PLAYER_OPTOUT_EDIT").description("Change player Promotional Opt-outs.").build());
        addRole(Role.builder().category(playerCategory).name("Player Auto Withdrawal Edit").role("PLAYER_AUTO_WITHDRAWAL_EDIT").description("Change player Auto Withdrawal.").build());

        addRole(Role.builder().category(playerCategory).name("Player Limit Edit").role("PLAYER_LIMIT_EDIT").description("Change player limit.").build());
        addRole(Role.builder().category(playerCategory).name("Player Balance Limit Edit").role("PLAYER_BALANCE_LIMIT_EDIT").description("Change player balance limit.").build());
        addRole(Role.builder().category(playerCategory).name("Player Deposit Limit Edit").role("PLAYER_DEPOSIT_LIMIT_EDIT").description("Change player deposit limit.").build());
        addRole(Role.builder().category(playerCategory).name("Player Play-Time Limit Edit").role("PLAYER_PLAY_TIME_LIMIT_EDIT").description("Change player Play-Time limit.").build());
        addRole(Role.builder().category(playerCategory).name("Player Verification Status Change").role("PLAYER_VERIFICATION_STATUS").description("Change player verification status.").build());
        addRole(Role.builder().category(playerCategory).name("Player Biometrics Status Change").role("PLAYER_BIOMETRICS_STATUS").description("Change player biometrics status.").build());
        addRole(Role.builder().category(playerCategory).name("Player NIN by Phone Verification").role("NIN_PHONE_MANUAL_VERIFY").description("Submit player NIN by phone for Verification.").build());
        addRole(Role.builder().category(playerCategory).name("Player Bvn Verification").role("PAYSTACK_BVN_MANUAL_VERIFY").description("Submit player bvn for Verification.").build());
        addRole(Role.builder().category(playerCategory).name("Player age verification toggle ").role("PLAYER_VALIDATE_AGE").description("Toggle player Age Verification").build());
        addRole(Role.builder().category(playerCategory).name("Player address verification toggle ").role("PLAYER_VALIDATE_ADDRESS").description("Toggle player Address Verification").build());
        addRole(Role.builder().category(playerCategory).name("Player Note Add").role("PLAYER_NOTE_ADD").description("Add player note").build());
        addRole(Role.builder().category(playerCategory).name("Player mark as test").role("PLAYER_MARK_TEST").description("Player mark test.").build());
        addRole(Role.builder().category(playerCategory).name("Player unmark test").role("PLAYER_UNMARK_TEST").description("Player unmark test.").build());
        addRole(Role.builder().category(playerCategory).name("User edit player notes").role("PLAYER_NOTES_EDIT").description("Edit other users notes").build());

        addRole(Role.builder().category(playerCategory).name("Player dashboard view").role("PLAYER_DASHBOARD_VIEW").description("Player dashboard view").build());
        addRole(Role.builder().category(playerCategory).name("Player comments view").role("PLAYER_COMMENTS_VIEW").description("Player comments view").build());
        addRole(Role.builder().category(playerCategory).name("Player bonus history view").role("PLAYER_BONUS_HISTORY_VIEW").description("Player bonus history view").build());
        addRole(Role.builder().category(playerCategory).name("Player login history view").role("PLAYER_LOGIN_EVENTS_VIEW").description("Player login history view").build());
        addRole(Role.builder().category(playerCategory).name("Player mail history view").role("PLAYER_MAIL_HISTORY_VIEW").description("Player mail history view").build());
        addRole(Role.builder().category(playerCategory).name("Player sms history view").role("PLAYER_SMS_HISTORY_VIEW").description("Player sms history view").build());
        addRole(Role.builder().category(playerCategory).name("Player documents view").role("PLAYER_DOCUMENTS_VIEW").description("Player documents view").build());
        addRole(Role.builder().category(playerCategory).name("Player old documents view").role("PLAYER_OLD_DOCUMENTS_VIEW").description("Player old documents view").build());


      addRole(Role.builder().category(playerCategory).name("Player events view").role("PLAYER_EVENTS_VIEW").description("Player events view").build());
        addRole(Role.builder().category(playerCategory).name("Player notifications view").role("PLAYER_NOTIFICATIONS_VIEW").description("Player notifications view").build());
        addRole(Role.builder().category(playerCategory).name("Player missions view").role("PLAYER_MISSIONS_VIEW").description("Player missions view").build());
        addRole(Role.builder().category(playerCategory).name("Player referrals view").role("PLAYER_REFERRALS_VIEW").description("Player referrals view").build());
        addRole(Role.builder().category(playerCategory).name("Player cashier transactions view").role("PLAYER_CASHIER_TRANSACTIONS_VIEW").description("Player cashier transactions view").build());
        addRole(Role.builder().category(playerCategory).name("Player notes view").role("PLAYER_NOTES_VIEW").description("Player notes view").build());
        addRole(Role.builder().category(playerCategory).name("Player responsible gaming view").role("PLAYER_RESPONSIBLE_GAMING_VIEW").description("Player responsible gaming view").build());
        addRole(Role.builder().category(playerCategory).name("Player datafeed resend").role("PLAYER_DATAFEED_RESEND").description("Player datafeed resend").build());
        addRole(Role.builder().category(playerCategory).name("Player bonuses view").role("PLAYER_BONUSES_VIEW").description("Player bonuses view").build());

        Category loginEvents = Category.builder().name("Login History").description("Operations related to login history.").build();
        addRole(Role.builder().category(loginEvents).name("Login History View").role("LOGINEVENTS_VIEW").description("View Login History Details").build());

        addRole(Role.builder().category(playerCategory).name("Ability To Export Player-Info Data").role("PLAYER_INFO_DATA").description("Ability To Export Player-Info Data").build());

        Category signupEvents = Category.builder().name("Signup Events").description("Operations related to signup events.").build();
        addRole(Role.builder().category(signupEvents).name("Signup Events View").role("SIGNUPEVENTS_VIEW").description("View Signup Event Details").build());

        addRole(Role.builder().category(playerCategory).name("Player Comments Views").role("PLAYER_COMMENT_LIST").description("View comments for a player.").build());
        addRole(Role.builder().category(playerCategory).name("Player last comment Views").role("PLAYER_LAST_COMMENT_LIST").description("View last comment for a player.").build());

        Category closureReasons = Category.builder().name("Closure reasons").description("Operations related to closure reasons.").build();
        addRole(Role.builder().category(closureReasons).name("Closure reasons Add").role("CLOSURE_REASONS_ADD").description("Add Closure Reasons Details").build());
        addRole(Role.builder().category(closureReasons).name("Closure reasons View").role("CLOSURE_REASONS_VIEW").description("View Closure Reasons Details").build());
        addRole(Role.builder().category(closureReasons).name("Closure reasons Edit").role("CLOSURE_REASONS_EDIT").description("Edit Closure Reasons Details").build());
        addRole(Role.builder().category(closureReasons).name("Closure reasons Delete").role("CLOSURE_REASONS_DELETE").description("Delete Closure Reasons Details").build());

        Category playerLinking = Category.builder().name("Player Links").description("Operations related to player linking.").build();
        addRole(Role.builder().category(playerLinking).name("Add player linking").role("PLAYER_LINK_ADD").description("Add player linking").build());
        addRole(Role.builder().category(playerLinking).name("View player linking").role("PLAYER_LINK_VIEW").description("View player linking").build());
        addRole(Role.builder().category(playerLinking).name("Edit player linking").role("PLAYER_LINK_EDIT").description("Edit player linking").build());
        addRole(Role.builder().category(playerLinking).name("Delete player linking").role("PLAYER_LINK_DELETE").description("Delete player linking").build());

        Category experimentalFeatures = Category.builder().name("Experimental Features").description("Allow users to see and access the new LBO experimental features.").build();
        addRole(Role.builder().category(experimentalFeatures).name("Experimental features enabled").role("EXPERIMENTAL_FEATURES_ENABLED").description("Enable experimental features for player").build());

        Category playerProtectionCategory = Category.builder().name("Player Protection").description("Player Protection Operations").build();
        addRole(Role.builder().category(playerProtectionCategory).name("Player Protection View").role("PLAYER_PROTECTION_VIEW").description("View all available player protections").build());
    }

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        // @formatter:off
        http.authorizeRequests()
                .antMatchers("/{domainName}/users/documents/downloadFile").permitAll()
                .antMatchers(HttpMethod.GET,"/{domainName}/users/documents/admin/listUserDocumentsExternal").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_DOCUMENT_EXTERNAL_LIST', 'PLAYER_DOCUMENTS_VIEW')")
                .antMatchers(HttpMethod.GET,"/{domainName}/users/documents/admin/listUserDocumentsInternal").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_DOCUMENT_INTERNAL_LIST', 'PLAYER_DOCUMENTS_VIEW')")
                .antMatchers(HttpMethod.POST,"/{domainName}/users/documents/admin/createDocument").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_DOC_ADD')")
                .antMatchers(HttpMethod.POST,"/{domainName}/users/documents/admin/saveFile").access("@lithiumSecurity.hasDomainRole(authentication, #domainName,'PLAYER_DOC_EDIT')")
                .antMatchers(HttpMethod.POST,"/{domainName}/users/documents/admin/updateDocument").access("@lithiumSecurity.hasDomainRole(authentication, #domainName,'PLAYER_DOC_EDIT')")

                .antMatchers("/profile").authenticated()
                .antMatchers("/profile/v2/**").authenticated()
                .antMatchers("/profile/**").authenticated()
                .antMatchers("/frontend/profile/**").authenticated()
                .antMatchers("/security/**").authenticated()
                .antMatchers("/roles/**").authenticated()
                .antMatchers("/status/**").authenticated()
                .antMatchers(HttpMethod.POST, "/frontend/{domainName}/register/*").permitAll() // Basic Auth token check inside API for all register versions
                .antMatchers(HttpMethod.POST, "/frontend/{domainName}/validate/mail/*").permitAll()
                .antMatchers(HttpMethod.POST, "/players/{domainName}/register").permitAll()
                .antMatchers(HttpMethod.POST, "/frontend/players/{domainName}/register/incomplete/v1").permitAll()
                .antMatchers(HttpMethod.POST, "/players/{domainName}/register/incomplete/v1").permitAll()
                .antMatchers(HttpMethod.GET, "/limitsysacc/**").authenticated()
                .antMatchers(HttpMethod.POST, "/limitsysacc/**").authenticated()
                .antMatchers(HttpMethod.POST, "/frontend/restrictions").authenticated()
                .antMatchers(HttpMethod.GET, "/players/{domainName}/find/incomplete").permitAll()
                .antMatchers(HttpMethod.GET, "/players/{domainName}/isunique").permitAll()
                .antMatchers(HttpMethod.GET, "/players/{domainName}/isemailunique").permitAll()
                .antMatchers(HttpMethod.POST, "/players/{domainName}/ismobileunique").permitAll()
                .antMatchers(HttpMethod.POST, "/players/{domainName}/is-password-ok").permitAll()
                .antMatchers(HttpMethod.POST, "/players/{domainName}/is-password-ok/v2").permitAll()
                .antMatchers(HttpMethod.GET, "/players/{domainName}/signupjourneytype").permitAll()
                .antMatchers(HttpMethod.GET, "/domain/{domainName}/groups").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GROUPS_LIST','GROUP_VIEW','GROUP_EDIT','USER_VIEW','USER_EDIT','USER_ADD')")
                .antMatchers(HttpMethod.POST, "/domain/{domainName}/groups").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GROUP_ADD')")
                .antMatchers(HttpMethod.GET, "/domain/{domainName}/group/*").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GROUP_VIEW','GROUP_EDIT')")
                .antMatchers(HttpMethod.GET, "/domain/{domainName}/group/*/changelogs").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GROUP_VIEW','GROUP_EDIT')")
                .antMatchers(HttpMethod.POST, "/domain/{domainName}/group/*").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GROUP_EDIT')")
                .antMatchers(HttpMethod.POST, "/domain/{domainName}/group/*/enabled/*").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GROUP_ENABLE')")
                .antMatchers(HttpMethod.POST, "/domain/{domainName}/group/*/remove").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GROUP_DELETE')")
                .antMatchers(HttpMethod.GET, "/domain/{domainName}/group/*/grds").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GROUP_ROLES_VIEW', 'GROUP_ROLES_EDIT')")
                .antMatchers(HttpMethod.POST, "/domain/{domainName}/group/*/grd/*/change/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GROUP_ROLES_EDIT')")
                .antMatchers(HttpMethod.POST, "/domain/{domainName}/group/*/removeRole/*").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GROUP_ROLES_REMOVE')")
                .antMatchers(HttpMethod.GET, "/domain/{domainName}/group/*/roles/*").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GROUP_ROLES_ADD')")
                .antMatchers(HttpMethod.POST, "/domain/{domainName}/group/*/addrole/*").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GROUP_ROLES_ADD')")
                .antMatchers(HttpMethod.GET, "/domain/{domainName}/group/*/users/table").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GROUP_USERS_VIEW', 'GROUP_USERS_EDIT')")
                .antMatchers(HttpMethod.POST, "/domain/{domainName}/group/*/users/add/*").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GROUP_USERS_ADD')")
                .antMatchers(HttpMethod.POST, "/domain/{domainName}/group/*/users/remove/*").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GROUP_USERS_REMOVE')")

                .antMatchers(HttpMethod.GET, "/players/search").access("@lithiumSecurity.hasRole(authentication, 'PLAYER_VIEW', 'PLAYER_EDIT')")
                .antMatchers("/players/table").authenticated() // Method does its own internal role validation cross domain

                .antMatchers(HttpMethod.GET, "/backoffice/players/tag/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'USER_VIEW', 'PLAYER_VIEW', 'PLAYER_TAG_EDIT')")
                .antMatchers(HttpMethod.POST, "/backoffice/players/tag/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_TAG_EDIT')")
                .antMatchers(HttpMethod.DELETE, "/backoffice/players/tag/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_TAG_EDIT')")
//

                .antMatchers(HttpMethod.POST, "/admin/passwordreset/clearFailedResetCount").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_CLEAR_FAILED_RESET')")

                .antMatchers(HttpMethod.GET, "/{domainName}/users/list").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'USER_VIEW', 'USER_EDIT', 'PLAYER_VIEW', 'PLAYER_EDIT', 'CASHIER_CONFIG', 'CASHIER_CONFIG_ADD')")

                .antMatchers(HttpMethod.POST, "/{domainName}/users").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'USER_ADD', 'PLAYER_ADD')")

                .antMatchers(HttpMethod.POST,"/{domainName}/users/{id}/saveverificationstatus").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VERIFICATION_STATUS')")
                .antMatchers(HttpMethod.PUT,"/{domainName}/users/{id}/biometrics-status").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_BIOMETRICS_STATUS')")


            .antMatchers(HttpMethod.POST, "/{domainName}/users/{id}/toggleMobileValidation").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VALIDATE_MOBILE')")
                .antMatchers(HttpMethod.POST, "/{domainName}/users/{id}/toggleEmailValidation").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VALIDATE_EMAIL')")
                .antMatchers(HttpMethod.POST, "/{domainName}/users/{id}/toggle-sow-validation").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_ALLOW_SOW_DOCUMENT')")
                .antMatchers(HttpMethod.POST, "/{domainName}/users/{id}/toggleAgeVerification").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VALIDATE_AGE')")
                .antMatchers(HttpMethod.POST, "/{domainName}/users/{id}/toggleAddressVerification").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VALIDATE_ADDRESS')")
                .antMatchers(HttpMethod.POST, "/{domainName}/users/{id}/add-note").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_NOTE_ADD')")
                .antMatchers(HttpMethod.POST, "/{domainName}/users/{id}/comments/add").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_COMMENT_ADD')")
                .antMatchers(HttpMethod.POST, "/{domainName}/users/{id}/saveaddress").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_ADDRESS_EDIT')")
                .antMatchers(HttpMethod.POST, "/{domainName}/users/{id}/updatecountryofbirth").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_COUNTRY_OF_BIRTH_EDIT')")
                .antMatchers(HttpMethod.POST, "/{domainName}/users/{id}/opt/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_OPTOUT_EDIT')")
                .antMatchers(HttpMethod.POST, "/{domainName}/users/{id}/savestatus/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_STATUS')")
                .antMatchers(HttpMethod.POST, "/{domainName}/users/{id}/update-failed-login-block/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'USER_EDIT', 'PLAYER_EDIT', 'PLAYER_INFO_EDIT')")
                .antMatchers(HttpMethod.POST, "/{domainName}/users/{id}/toggleAutoWithdrawalAllowed").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_AUTO_WITHDRAWAL_EDIT')")
                .antMatchers(HttpMethod.POST, "/{domainName}/users/{id}/test-account/true").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_MARK_TEST')")
                .antMatchers(HttpMethod.POST, "/{domainName}/users/{id}/test-account/false").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_UNMARK_TEST')")
                .antMatchers(HttpMethod.GET, "/{domainName}/users/{id}/loginevents/table").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_LOGIN_EVENTS_VIEW')")

                .antMatchers(HttpMethod.GET, "/{domainName}/users/**")
                .access("@lithiumSecurity.hasDomainRole("
                    + "authentication, #domainName, 'USER_VIEW', 'USER_EDIT', 'PLAYER_VIEW', 'PLAYER_EDIT', 'PLAYER_INFO_EDIT')")
                .antMatchers(HttpMethod.GET, "/{domainName}/users/list").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'USER_VIEW', 'USER_EDIT', 'PLAYER_VIEW', 'PLAYER_EDIT', 'PLAYER_INFO_EDIT', 'ACCESSRULES_VIEW')")
                .antMatchers(HttpMethod.PUT, "/{domainName}/users/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'USER_EDIT', 'PLAYER_EDIT', 'PLAYER_INFO_EDIT')")
                .antMatchers(HttpMethod.POST, "/{domainName}/users/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'USER_EDIT', 'PLAYER_EDIT', 'PLAYER_INFO_EDIT', 'PLAYER_PASSWD', 'PLAYER_DOB_EDIT')")
                .antMatchers(HttpMethod.POST, "/{domain}/users/{id}/changedateofbirth").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'USER_EDIT', 'PLAYER_EDIT', 'PLAYER_DOB_EDIT')")

                .antMatchers(HttpMethod.POST, "/backoffice/changelogs/global/update-note").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_NOTES_EDIT')")

                .antMatchers(HttpMethod.POST, "/backoffice/players/{domainName}/{id}/redo-email-validation").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_EDIT')")
                .antMatchers(HttpMethod.POST, "/backoffice/players/{domainName}/{id}/v2/redo-email-validation").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_EDIT')")
                .antMatchers(HttpMethod.POST, "/backoffice/players/{domainName}/{id}/redo-mobile-phone-validation").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_EDIT')")
                .antMatchers(HttpMethod.GET, "/{domainName}/players/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT')")
//			.antMatchers(HttpMethod.PUT, "/{domainName}/players/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_EDIT')")
//			.antMatchers(HttpMethod.POST, "/{domainName}/players/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_EDIT', 'PLAYER_PASSWD', 'PLAYER_STATUS')")

                .antMatchers(HttpMethod.POST, "/userevent/system/{domainName}/{userName}/**").authenticated()
//			.access("@lithiumSecurity.authenticatedSystem(authentication)")
                .antMatchers(HttpMethod.GET, "/userevent/system/{domainName}/{userName}/**").authenticated()
//			.access("@lithiumSecurity.authenticatedSystem(authentication)")
                .antMatchers(HttpMethod.GET, "/userevent/{domainName}/**").authenticated()

                .antMatchers(HttpMethod.POST, "/{domainName}/{emailOrUsernameOrPhoneNumber}/emailvalidation/**").permitAll()
                .antMatchers(HttpMethod.POST, "/{domainName}/{emailOrUsernameOrPhoneNumber}/emailvalidation/v2/**").permitAll()
                .antMatchers(HttpMethod.POST, "/{domainName}/{emailOrUsernameOrPhoneNumber}/passwordreset/**").permitAll()
                .antMatchers(HttpMethod.POST, "/{domainName}/{emailOrUsernameOrPhoneNumber}/cellphonevalidation/**").permitAll()
                .antMatchers(HttpMethod.POST, "/passwordreset/**").permitAll()

                .antMatchers(HttpMethod.GET, "/loginevents/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'LOGINEVENTS_VIEW')")

                .antMatchers("/signupevents/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'SIGNUPEVENTS_VIEW')")
                .antMatchers(HttpMethod.GET, "/signupevent/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'SIGNUPEVENTS_VIEW')")

                .antMatchers("/affiliates/{domainName}/**").access("@lithiumSecurity.authenticatedSystem(authentication)")

            .antMatchers(HttpMethod.POST,"/backoffice/affiliates/**").authenticated()


            .antMatchers("/{domainName}/tranta/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_BALANCE_ADJUST', 'MASS_PLAYER_UPDATE_VIEW')")

                .antMatchers("/userapi/**").access("@lithiumSecurity.authenticatedSystem(authentication)")

                .antMatchers("/userapiinternal/**").access("@lithiumSecurity.authenticatedSystem(authentication)")
            .antMatchers("/system/user-api-internal/**").access("@lithiumSecurity.authenticatedSystem(authentication)")
            .antMatchers("/system/historic-registration-ingestion/**").access("@lithiumSecurity.authenticatedSystem(authentication)")


                .antMatchers(HttpMethod.GET, "/users/user").access("@lithiumSecurity.authenticatedSystem(authentication)")
                .antMatchers(HttpMethod.DELETE, "/users/d/**").authenticated()

                .antMatchers("/promotions/opt/**").permitAll()

                .antMatchers("/timezones/**").permitAll()

                .antMatchers(HttpMethod.GET, "/{domainName}/incompleteusersforreport/**").access("@lithiumSecurity.authenticatedSystem(authentication)")

                .antMatchers(HttpMethod.GET, "/incompleteusers/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_VIEW', 'PLAYER_EDIT')")
                .antMatchers(HttpMethod.GET, "/{domainName}/incompleteusers/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT')")

                .antMatchers(HttpMethod.POST, "/backoffice/{domainName}/balance-adjust").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_BALANCE_ADJUST', 'PLAYER_BALANCE_ADJUST_WRITE_OFF')")
                .antMatchers(HttpMethod.POST, "/backoffice/{domainName}/profile/opt/").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'USER_OPT', 'PLAYER_OPT')")
                .antMatchers(HttpMethod.POST, "/backoffice/{domainName}/profile/password-reset").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_PASSWORD_RESET')")
                .antMatchers(HttpMethod.GET, "/backoffice/datafeed-resend-account-create").access("@lithiumSecurity.hasRole(authentication, 'PLAYER_DATAFEED_RESEND')")
                .antMatchers(HttpMethod.POST, "/players/{domainName}/isfullnameunique").permitAll()
                .antMatchers(HttpMethod.POST, "/frontend/player/logout").authenticated()

                .antMatchers("/backoffice/{domainName}/affiliates/{id}/references").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT')")
                .antMatchers(HttpMethod.GET, "/backoffice/{domainName}/closure-reasons-crud/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CLOSURE_REASONS_VIEW', 'CLOSURE_REASONS_ADD', 'CLOSURE_REASONS_EDIT', 'CLOSURE_REASONS_DELETE', 'ADMIN')")
                .antMatchers(HttpMethod.POST, "/backoffice/{domainName}/closure-reasons-crud/add").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CLOSURE_REASONS_ADD', 'ADMIN')")
                .antMatchers(HttpMethod.POST, "/backoffice/{domainName}/closure-reasons-crud/save").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CLOSURE_REASONS_EDIT', 'ADMIN')")
                .antMatchers(HttpMethod.DELETE, "/backoffice/{domainName}/closure-reasons-crud/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CLOSURE_REASONS_DELETE', 'ADMIN')")

                .antMatchers("/frontend/player/**").authenticated()
                .antMatchers("/frontend/players/{domainName}/isfullnameunique").permitAll()
                .antMatchers("/frontend/players/{domainName}/v2/is-full-name-unique").permitAll()
                .antMatchers("/frontend/players/{domainName}/v2/is-username-unique").permitAll()
                .antMatchers("/frontend/players/{domainName}/v2/is-email-unique").permitAll()
                .antMatchers("/frontend/players/**").authenticated()

                .antMatchers(HttpMethod.POST,"/{domainName}/comments/{id}/add").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_COMMENT_ADD')")
                .antMatchers(HttpMethod.GET,"/{domainName}/comments/{id}/table").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_COMMENT_LIST', 'PLAYER_COMMENTS_VIEW')")
                .antMatchers(HttpMethod.GET,"/{domainName}/comments/{id}/last").access("@lithiumSecurity.hasDomainRole(authentication, #domainName,'PLAYER_LAST_COMMENT_LIST', 'PLAYER_NOTE_ADD')")

                .antMatchers("/backoffice/account-status/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'PLAYER_EDIT')")

                //TODO: Determine a healthy way to incorporate domain checking (since this is possibly ecosystem related there will need to be additional checks for linking)
                .antMatchers( "/backoffice/user-link/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_VIEW', 'PLAYER_EDIT')")
                .antMatchers(HttpMethod.GET, "/backoffice/user-link/**").access("@lithiumSecurity.hasRole(authentication, 'PLAYER_VIEW', 'PLAYER_EDIT','PLAYER_LINK_VIEW')")
                .antMatchers(HttpMethod.POST, "/backoffice/user-link/**").access("@lithiumSecurity.hasRole(authentication, 'PLAYER_VIEW', 'PLAYER_EDIT','PLAYER_LINK_ADD')")

            .antMatchers( "/backoffice/play-time-limit/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_VIEW', 'PLAYER_EDIT')")
            .antMatchers(HttpMethod.GET, "/frontend/play-time-limit/**").authenticated()
            .antMatchers(HttpMethod.POST,"/frontend/play-time-limit/**").authenticated()
            .antMatchers( "/backoffice/playtime-limit/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_VIEW', 'PLAYER_EDIT')")
            .antMatchers(HttpMethod.POST, "/backoffice/playtime-limit/v2/migrate-v1-data").access("@lithiumSecurity.hasRoleInTree(authentication, 'ADMIN')")
            .antMatchers(HttpMethod.POST, "/backoffice/playtime-limit/v2/configuration/set").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_EDIT')")
            .antMatchers(HttpMethod.POST, "/backoffice/playtime-limit/v2/configuration/get").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_VIEW')")
            .antMatchers(HttpMethod.GET, "/backoffice/playtime-limit/v2/configuration/remove-pending").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_EDIT')")
            .antMatchers(HttpMethod.GET, "/frontend/playtime-limit/**").authenticated()
            .antMatchers(HttpMethod.POST,"/frontend/playtime-limit/**").authenticated()
            .antMatchers("/system/playtime-limit/**").access("@lithiumSecurity.authenticatedSystem(authentication)")


            .antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)")
            .antMatchers("/backoffice/user-jobs/**").access("@lithiumSecurity.hasRole(authentication,'PLAYER_EDIT')")

            .antMatchers(HttpMethod.GET, "/backoffice/profile").authenticated()

            .antMatchers(HttpMethod.POST, "/backoffice/profile").access("@lithiumSecurity.hasRoleInTree(authentication, 'USER_EDIT')")
            .antMatchers(HttpMethod.POST, "/backoffice/profile/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'USER_EDIT')")

            .antMatchers("/complete-placeholders/**").access("@lithiumSecurity.authenticatedSystem(authentication)")

            .antMatchers(HttpMethod.GET,"/data-migration-job/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'ADMIN')")

            .antMatchers( "/backoffice/biometrics-statuses/**").access("@lithiumSecurity.hasRoleInTree(authentication,'PLAYER_VIEW', 'PLAYER_EDIT')")

        ;
        // @formatter:on
      http.authorizeRequests().antMatchers("/external/**").permitAll();
    }
}
