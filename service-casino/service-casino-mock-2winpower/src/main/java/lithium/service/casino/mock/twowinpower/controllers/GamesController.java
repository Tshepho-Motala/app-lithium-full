package lithium.service.casino.mock.twowinpower.controllers;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import lithium.service.casino.provider.twowinpower.data.GameListResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class GamesController {
//	@Autowired
//	@Qualifier(\"lithium.service.casino.mock.twowinpower.resttemplate\")
//	private RestTemplate restTemplate;
//	
//	@Autowired
//	private Configuration conf;
//	@Autowired
//	private TwoWinPowerMockService service;
	
	@RequestMapping(value="/games", consumes="application/x-www-form-urlencoded", produces="application/json")
	public GameListResponse gameList(
		HttpServletRequest request,
		HttpServletResponse response
	) throws Exception {
		log.info("Fake game list");
		Enumeration<String> headers = request.getHeaderNames();
		while (headers.hasMoreElements()) {
			String header = headers.nextElement();
			response.setHeader(header, request.getHeader(header));
		}
		String jsonResponse = "{\"items\":[{\"uuid\":\"fe0733b4921b38b31f91656c7f5c983bb1fbe3a0\",\"name\":\"Caesars Empire\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/RTG/9741bd78daeb1563b58fc754517d5632.png\",\"type\":\"slots\",\"provider\":\"RTG\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0},{\"uuid\":\"a23fafb6b0808be4337f45d7094399ff7497153d\",\"name\":\"Cleopatras Gold\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/RTG/542d328925ed0764ddd08c058fca43ba.png\",\"type\":\"slots\",\"provider\":\"RTG\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0},{\"uuid\":\"e25eaa39c584f1ca2e653d9d9d143133ccc05c44\",\"name\":\"Diamond Dozen\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/RTG/3cf082118529a3ae2a86f5bd94372f3c.png\",\"type\":\"slots\",\"provider\":\"RTG\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0},{\"uuid\":\"57fdb6aeca8f53d635ad079a6c176df85768c565\",\"name\":\"Lions Liar\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/RTG/75c1dd5e8c7e26f02141db8856a50be5.png\",\"type\":\"slots\",\"provider\":\"RTG\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0},{\"uuid\":\"4556b767bba24045671f1a0eb8c06d9c489495af\",\"name\":\"Mister Money\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/RTG/4f8c9f71b59e15c376c75d70b1be0d02.png\",\"type\":\"slots\",\"provider\":\"RTG\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0},{\"uuid\":\"d93446313a18d379bb47d35c14f9b1051a00ef9d\",\"name\":\"Red Sands\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/RTG/023c43ca30b3810d5925afa4f298ecd9.png\",\"type\":\"slots\",\"provider\":\"RTG\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0},{\"uuid\":\"0307c6069d8596d655fce7c4a2d4425b28bb7975\",\"name\":\"Tiger Treasures\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/RTG/ce8b3680de01b77152fc6697a9713a65.png\",\"type\":\"slots\",\"provider\":\"RTG\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0},{\"uuid\":\"b8e00cf4af131894b61653557999c87bae497558\",\"name\":\"Aladdins Wishes\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/RTG/ce80be41e730526110df0c9d4ac0762c.png\",\"type\":\"slots\",\"provider\":\"RTG\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0},{\"uuid\":\"a2a29b44579f5759b64ded9b6c3496a0351c2517\",\"name\":\"Aztecs Treasure\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/RTG/a6fc76647112fb96c44a1e60f0eca8de.png\",\"type\":\"slots\",\"provider\":\"RTG\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0},{\"uuid\":\"6785c074d120df4b2fa04a622d7c069ebe99c269\",\"name\":\"Crystal Waters\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/RTG/4b34b18c71f60f273dd721e91e49eea8.png\",\"type\":\"slots\",\"provider\":\"RTG\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0},{\"uuid\":\"05601b6413bd843d05c64e08afb6b50ccc38337b\",\"name\":\"DerbyDollars\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/RTG/a982ed0587b1e11e62b22a65810e3211.png\",\"type\":\"slots\",\"provider\":\"RTG\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0},{\"uuid\":\"8adeaf14e1aac7326fb4244de4af3d435d17fe16\",\"name\":\"Gold Beard\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/RTG/25f8554972f3da3adbae6e5945916fc2.png\",\"type\":\"slots\",\"provider\":\"RTG\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0},{\"uuid\":\"c2518a6fe3eb7dcdf445ebe2e92d2a23a3d15554\",\"name\":\"Green Light\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/RTG/0c742009d3dc88c83b8c3664589997b4.png\",\"type\":\"slots\",\"provider\":\"RTG\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0},{\"uuid\":\"5e90499b0a1ee041e39d22371575d3dab846647b\",\"name\":\"Rain Dance\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/RTG/f40c9c3624e8aabd7ccf3a818fae29b3.png\",\"type\":\"slots\",\"provider\":\"RTG\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0},{\"uuid\":\"7fe0f47b63a28ef9dfab54d56e3b6c2c0acdc72e\",\"name\":\"Ronin\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/RTG/c2cb8c1aaf013cb8968352699ec8c080.png\",\"type\":\"slots\",\"provider\":\"RTG\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0},{\"uuid\":\"223df45afe894b44306bc72f8d09efb8850b20ce\",\"name\":\"Coyote Cash\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/RTG/fad424ab67696e3580f99e0586d6305d.png\",\"type\":\"slots\",\"provider\":\"RTG\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0},{\"uuid\":\"c1834336d9126c869f896ec481663370c9be7845\",\"name\":\"Fruit Frenzy\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/RTG/5f530b969a7642915f5435b87a4b4d46.png\",\"type\":\"slots\",\"provider\":\"RTG\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0},{\"uuid\":\"83478fb4c74cf57f659a448164f4a93841ec2e4d\",\"name\":\"Crazy Monkey\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/Igrosoft/8e61b1f6bb525e8b4920e70a735738a7.png\",\"type\":\"slots\",\"provider\":\"Igrosoft\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0},{\"uuid\":\"dd90cd84de67d97cd66c1f84073425a95351370b\",\"name\":\"Fruit Cocktail\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/Igrosoft/a48d28d4a21164ea8b622a84e966faee.png\",\"type\":\"slots\",\"provider\":\"Igrosoft\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0},{\"uuid\":\"de16439677df86e9dce407dbe0a6ebd27be3fdb9\",\"name\":\"Garage\",\"image\":\"https://staging.gamerouter.pw/api/images/games/feebf2ce8e41247d0a2c533af8bd4e6a/Igrosoft/b61e00111f85be948ef2a2adc9aeae0e.png\",\"type\":\"slots\",\"provider\":\"Igrosoft\",\"technology\":\"Flash\",\"has_lobby\":0,\"is_mobile\":0}],\"_links\":{\"self\":{\"href\":\"https://staging.gamerouter.pw/api/index.php/v1/games/index?page=1\"},\"next\":{\"href\":\"https://staging.gamerouter.pw/api/index.php/v1/games/index?page=2\"},\"last\":{\"href\":\"https://staging.gamerouter.pw/api/index.php/v1/games/index?page=26\"}},\"_meta\":{\"totalCount\":513,\"pageCount\":26,\"currentPage\":1,\"perPage\":20}}";
		
		GameListResponse gameListResponse = new ObjectMapper().readValue(jsonResponse, GameListResponse.class);
		log.info("gameListResponse :: "+gameListResponse);
		return gameListResponse;
		
//		return GameListResponse.builder().items(
//			Arrays.asList(
//				Game.builder().name("game1").provider("provider1").uuid("uuid1").isMobile(0).hasLobby(0).type("Slots").build(),
//				Game.builder().name("game2").provider("provider2").uuid("uuid2").isMobile(1).hasLobby(1).type("Slots").build(),
//				Game.builder().name("game3").provider("provider2").uuid("uuid3").isMobile(0).hasLobby(1).type("Slots").build(),
//				Game.builder().name("game4").provider("provider1").uuid("uuid4").isMobile(0).hasLobby(0).type("Slots").build()
//			)
//		).build();
	}
}