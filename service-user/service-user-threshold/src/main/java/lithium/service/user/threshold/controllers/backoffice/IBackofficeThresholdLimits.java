package lithium.service.user.threshold.controllers.backoffice;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.math.BigDecimal;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.user.threshold.client.dto.ThresholdDto;
import lithium.service.user.threshold.data.entities.Threshold;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SecurityRequirement( name = "LithiumTokenUtil" )
@RequestMapping( "/backoffice/threshold/loss-limit/{domainName}" )
public interface IBackofficeThresholdLimits {

  @Operation( summary = "Find a threshold record for a domain and a specific granularity, and optionally for a specified age range.", tags = {"Loss Limits Setup"} )
  @ApiResponse( responseCode = "200", description = "Threshold found", content = {
      @Content( mediaType = APPLICATION_JSON_VALUE, schema = @Schema( implementation = ThresholdDto.class ) )} )
  @GetMapping( "/find" )
  ThresholdDto find(
      @Parameter( name = "domainName", required = true, description = "Brand details here", in = ParameterIn.PATH, example = "livescore_uk" ) @PathVariable( name = "domainName" ) String domainName,
      @Parameter( name = "granularity", required = true, description = "Granularity details here", in = ParameterIn.QUERY, style = ParameterStyle.SIMPLE, examples = {
          @ExampleObject( name = "GRANULARITY_DAY", value = "3", summary = "To find the daily threshold." ),
          @ExampleObject( name = "GRANULARITY_WEEK", value = "4", summary = "To find the weekly threshold." ),
          @ExampleObject( name = "GRANULARITY_MONTH", value = "2", summary = "To find the monthly threshold." )} ) @RequestParam( name = "granularity" ) Integer granularity,
      @Parameter( name = "ageMin", description = "If searching for a specific age based threshold, the min age required.", in = ParameterIn.QUERY, example = "18" ) @RequestParam( name = "ageMin", required = false ) Integer ageMin,
      @Parameter( name = "ageMax", description = "If searching for a specific age based threshold, the max age required.", in = ParameterIn.QUERY, example = "25" ) @RequestParam( name = "ageMax", required = false ) Integer ageMax,
      @Parameter( name = "eType", description = "If searching for deposit limit or loss limit threshold, eType is required.", in = ParameterIn.QUERY, example = "TYPE_DEPOSIT_LIMIT" ) @RequestParam( name = "eType", required = false ) String  eType,
      @Parameter( name = "lithiumTokenUtil", hidden = true ) LithiumTokenUtil lithiumTokenUtil);

  @Operation( summary = "Save a threshold record for a domain and a specific granularity, and optionally for a specified age range.", tags = {"Loss Limits Setup"} )
  @PostMapping( "/save" )
  ThresholdDto save(
      @PathVariable( name = "domainName" ) String domainName,
      @RequestParam( name = "id", required = false ) Threshold threshold,
      @RequestParam( name = "percentage",required = false ) BigDecimal percentage,
      @RequestParam( name = "amount", required = false ) BigDecimal amount,
      @RequestParam( name = "eType", required = false ) String eType,
      @RequestParam( name = "granularity" ) Integer granularity,
      @RequestParam( name = "ageMin", required = false ) Integer ageMin,
      @RequestParam( name = "ageMax", required = false ) Integer ageMax,
      @Parameter( name = "lithiumTokenUtil", hidden = true ) LithiumTokenUtil lithiumTokenUtil)
  throws Status500InternalServerErrorException;

  @Operation( summary = "Disable a threshold record for a domain and a specific granularity, and optionally for a specified age range.", tags = {"Loss Limits Setup"} )
  @PostMapping( "/disable" )
  ThresholdDto disable(
      @PathVariable( name = "domainName" ) String domainName,
      @RequestParam( name = "id", required = false ) Threshold threshold,@RequestParam( name = "eType", required = false ) String eType,
      @Parameter( name = "lithiumTokenUtil", hidden = true ) LithiumTokenUtil lithiumTokenUtil)
  throws Status500InternalServerErrorException;
}
