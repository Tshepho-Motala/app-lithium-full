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
import java.util.Date;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.threshold.client.dto.PlayerThresholdHistoryDto;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SecurityRequirement( name = "LithiumTokenUtil" )
@RequestMapping( "/backoffice/threshold/warnings/{domainName}" )
public interface IBackofficeThresholdWarnings {

  @PostMapping( "/find" )
  @Operation( summary = "Find applicable warnings issued to a player account.", tags = {"Player Thresholds"}, description = "", operationId = "find" )
  DataTableResponse<PlayerThresholdHistoryDto> find(@PathVariable( "domainName" ) String domainName,
      @RequestParam( required = false ) String playerGuid, @RequestParam( required = false ) String[] typeName,
      @RequestParam( required = false ) Integer granularity, @RequestParam( required = false )  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date dateStart,
      @RequestParam( required = false )  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date dateEnd, DataTableRequest tableRequest,
      @Parameter( name = "lithiumTokenUtil", hidden = true ) LithiumTokenUtil lithiumTokenUtil)
      throws Status500InternalServerErrorException;


  @Operation( summary = "Enable/disable notifications sent to player inbox.", tags = {"Player Thresholds"})
  @ApiResponse( responseCode = "200", description = "Returns current status of notifications flag on player.", content = {
      @Content( mediaType = APPLICATION_JSON_VALUE, schema = @Schema( implementation = Boolean.class ) )} )
  @PostMapping( "/set-notifications" )
  boolean setNotifications(
      @Parameter( name = "playerGuid", required = true, description = "Guid for player to toggle.", in = ParameterIn.QUERY, example = "livescore_uk/123" ) @RequestParam String playerGuid,
      @Parameter( name = "notifications", required = true, description = "Boolean to enable/disable the notifications for the player.", in = ParameterIn.QUERY, style = ParameterStyle.SIMPLE, examples = {
          @ExampleObject( name = "TRUE", value = "true", summary = "To enable notifications to player." ),
          @ExampleObject( name = "FALSE", value = "false", summary = "To disable notifications to player." )} ) @RequestParam boolean notifications,
      @Parameter( name = "lithiumTokenUtil", hidden = true ) LithiumTokenUtil lithiumTokenUtil)
  throws Status500InternalServerErrorException;

  @Operation( summary = "Retrieve current notifications settings for a player.", tags = {"Player Thresholds"})
  @PostMapping( "/get-notifications" )
  boolean getNotifications(
      @Parameter( name = "playerGuid", required = true, description = "Guid for player to toggle.", in = ParameterIn.QUERY, example = "livescore_uk/123" ) @RequestParam String playerGuid,
      @Parameter( name = "lithiumTokenUtil", hidden = true ) LithiumTokenUtil lithiumTokenUtil)
  throws Status500InternalServerErrorException;
}
