package lithium.service.kyc.provider.config;

import lithium.service.client.provider.ProviderConfigProperty;
import lithium.service.kyc.provider.objects.VerificationMethodType;

public interface KycTypeProviderProperty {
    ProviderConfigProperty PASSPORT_NUMBER = ProviderConfigProperty.builder()
            .name(VerificationMethodType.METHOD_PASSPORT.getValue())
            .required(true)
            .tooltip("Enable to allow Passport Number verification check")
            .dataType(Boolean.class)
            .version(1)
            .build();

    ProviderConfigProperty NATIONAL_ID = ProviderConfigProperty.builder()
            .name(VerificationMethodType.METHOD_NATIONAL_ID.getValue())
            .required(true)
            .tooltip("Enable to allow National Id verification check")
            .dataType(Boolean.class)
            .version(1)
            .build();

    ProviderConfigProperty NIN = ProviderConfigProperty.builder()
            .name(VerificationMethodType.METHOD_NIN.getValue())
            .required(true)
            .tooltip("Enable to allow NIN verification check")
            .dataType(Boolean.class)
            .version(1)
            .build();

    ProviderConfigProperty DRIVERS_LICENCE = ProviderConfigProperty.builder()
            .name(VerificationMethodType.METHOD_DRIVERS_LICENSE.getValue())
            .required(true)
            .tooltip("Enable to allow Driver's licence verification check")
            .dataType(Boolean.class)
            .version(1)
            .build();

    ProviderConfigProperty BANK_ACCOUNT = ProviderConfigProperty.builder()
            .name(VerificationMethodType.METHOD_BANK_ACCOUNT.getValue())
            .required(true)
            .tooltip("Enable to allow Bank Account verification check")
            .dataType(Boolean.class)
            .version(1)
            .build();

    ProviderConfigProperty BVN = ProviderConfigProperty.builder()
            .name(VerificationMethodType.METHOD_BVN.getValue())
            .required(true)
            .tooltip("Enable to allow BVN verification check")
            .dataType(Boolean.class)
            .version(1)
            .build();

    ProviderConfigProperty VOTER_ID = ProviderConfigProperty.builder()
            .name(VerificationMethodType.METHOD_VOTER_ID.getValue())
            .required(true)
            .tooltip("Enable to allow Voter-ID verification check")
            .dataType(Boolean.class)
            .version(1)
            .build();

	ProviderConfigProperty NIN_PHONE_NUMBER = ProviderConfigProperty.builder()
			.name(VerificationMethodType.METHOD_NIN_PHONE_NUMBER.getValue())
			.required(true)
			.tooltip("Enable to allow NIN verification check through mobile number")
			.dataType(Boolean.class)
			.version(1)
			.build();

}
