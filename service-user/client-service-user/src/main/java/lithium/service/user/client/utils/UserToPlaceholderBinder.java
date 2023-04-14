package lithium.service.user.client.utils;

import lithium.service.client.objects.placeholders.EntityToPlaceholderBinder;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.user.client.objects.User;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_ACCOUNT_STATUS;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_ADDRESS_VERIFIED;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_AGE_VERIFIED;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_CELLPHONE_NUMBER;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_CREATE_DATE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_DOB;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_EMAIL_ADDRESS;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_FIRST_NAME;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_GUID;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_LAST_NAME;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_LAST_NAME_PREFIX;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_NAME;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_PLAYER_LINK;


public class UserToPlaceholderBinder implements EntityToPlaceholderBinder {
    private final User user;

    public UserToPlaceholderBinder(User user) {
        this.user = user;
    }

    @Override
    public Set<Placeholder> completePlaceholders() {

        Set<Placeholder> placeholders = new HashSet<Placeholder>();

        placeholders.add(USER_NAME.from(user.getUsername()));
        placeholders.add(USER_GUID.from(user.guid()));
        placeholders.add(USER_FIRST_NAME.from(user.getFirstName()));
        placeholders.add(USER_LAST_NAME.from(user.getLastName()));
        placeholders.add(USER_LAST_NAME_PREFIX.from(user.getLastNamePrefix()));
        placeholders.add(USER_EMAIL_ADDRESS.from(user.getEmail()));
        placeholders.add(USER_CELLPHONE_NUMBER.from(user.getCellphoneNumber()));
        placeholders.add(USER_ACCOUNT_STATUS.from(user.getStatus().getName()));
        placeholders.add(USER_AGE_VERIFIED.from(nonNull(user.getAgeVerified()) && user.getAgeVerified() ? "Yes" : "No"));
        placeholders.add(USER_ADDRESS_VERIFIED.from(nonNull(user.getAgeVerified()) && user.getAddressVerified() ? "Yes" : "No"));
        placeholders.add(USER_DOB.from(ofNullable(user.getDateOfBirth()).map(DateTime::toDate).orElse(null)));
        placeholders.add(USER_PLAYER_LINK.from("/#/dashboard/players/" + user.getDomain().getName() + "/" + user.getId() + "/summary"));
        placeholders.add(USER_CREATE_DATE.from(user.getCreatedDate()));

        return placeholders;
    }

}
