package lithium.service.cdn.provider.google.service.storage.utils;

import static lithium.service.cdn.provider.google.service.utils.PredicateUtils.not;

import com.google.common.base.Strings;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lithium.service.cdn.provider.google.builders.PrefixBuilder;



public class BucketPrefixProcessor {

  private static final Pattern justSlashes = Pattern.compile("^/+$");

  //TODO: replace with single all-purpose validation pattern
  private static final Pattern allowedCharacters = Pattern.compile("[0-9a-zA-Z/ -]+$");
  public static final String INVALID_BUCKET_PREFIX = "The bucket prefix is currently invalid and needs to be changed";

  @Nonnull
  public static String prepare(@Nullable String bucketPrefix, @Nonnull String language) {
    if (Strings.isNullOrEmpty(bucketPrefix)) {
      return "";
    }

    bucketPrefix = bucketPrefix.replace("{lang}", language);

    return Optional.of(bucketPrefix)
        .filter(not(hasSlashesOnly()))
        .filter(not(hasSpecialCharacters()))
        .map(PrefixBuilder::build)
        .orElseThrow(() -> new RuntimeException(INVALID_BUCKET_PREFIX));
  }

  private static Predicate<String> hasSpecialCharacters() {
    return str -> !allowedCharacters.matcher(str).matches();
  }

  private static Predicate<String> hasSlashesOnly() {
    return str -> justSlashes.matcher(str).matches();
  }

}
