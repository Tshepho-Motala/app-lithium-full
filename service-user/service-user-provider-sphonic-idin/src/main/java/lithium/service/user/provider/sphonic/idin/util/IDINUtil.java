package lithium.service.user.provider.sphonic.idin.util;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IDINUtil {

    /**
     * This is a simple method for reformatting Netherlands cellphone numbers written in international format
     * @param cellphoneNumber
     * @return
     */
    public String cellphoneNumberRemoveInternationalFormat(String cellphoneNumber) {
        String removeSpace = cellphoneNumber.replaceAll("\\s", "");
        String patternReg = "^[+|00]*31";
        Pattern netherlandsIntFormat = Pattern.compile(patternReg);
        Matcher matcher = netherlandsIntFormat.matcher(removeSpace);
        if(matcher.find()) {
            String [] cellphoneWithoutCountryCode  = removeSpace.split(patternReg);
            String cellphoneWithoutNonNumericChars = cellphoneWithoutCountryCode[1].replaceAll("[^0-9]*", "");

            if(!cellphoneWithoutNonNumericChars.startsWith("0")) {
                StringBuilder sb = new StringBuilder("0");
                sb.append(cellphoneWithoutNonNumericChars);
                cellphoneNumber = sb.toString();
            } else {
                cellphoneNumber = cellphoneWithoutNonNumericChars;
            }
        }
        return cellphoneNumber;
    }
}
