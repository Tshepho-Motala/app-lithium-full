package lithium.service.cashier.stream;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.cashier.data.entities.Fees;
import lithium.service.cashier.data.entities.Image;
import lithium.service.cashier.data.entities.Limits;
import lithium.service.cashier.data.entities.Method;
import lithium.service.cashier.data.entities.MethodStage;
import lithium.service.cashier.data.entities.MethodStageField;
import lithium.service.cashier.data.entities.Processor;
import lithium.service.cashier.data.entities.ProcessorMethod;
import lithium.service.cashier.data.entities.ProcessorProperty;
import lithium.service.cashier.services.FeesService;
import lithium.service.cashier.services.ImageService;
import lithium.service.cashier.services.LimitsService;
import lithium.service.cashier.services.MethodService;
import lithium.service.cashier.services.MethodStageFieldService;
import lithium.service.cashier.services.MethodStageService;
import lithium.service.cashier.services.ProcessorMethodService;
import lithium.service.cashier.services.ProcessorService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableBinding(ProcessorRegisterQueueSink.class)
public class ProcessorRegisterQueueProcessor {
	@Autowired
	private MethodService methodService;
	@Autowired
	private ProcessorService processorService;
	@Autowired
	private ProcessorMethodService processorMethodService;
	@Autowired
	private FeesService feesService;
	@Autowired
	private LimitsService limitsService;
	@Autowired
	private ImageService imageService;
	@Autowired
	private MethodStageService stageService;
	@Autowired
	private MethodStageFieldService stageFieldService;
	
	@StreamListener(ProcessorRegisterQueueSink.INPUT)
	void handle(lithium.service.cashier.client.objects.transaction.dto.Processor sp) throws Exception {
		
		log.info("Processor received for registration: " + sp.getName());
		
		Processor p = processorService.findByCode(sp.getCode());
		if (p == null) p = new Processor();
		if (p.getFees() == null) p.setFees(new Fees());
		if (p.getLimits() == null) p.setLimits(new Limits());
		if (sp.getFees() == null) sp.setFees(new lithium.service.cashier.client.objects.Fees());
		if (sp.getLimits() == null) sp.setLimits(new lithium.service.cashier.client.objects.Limits());
		
		p.setCode(sp.getCode());
		p.setName(sp.getName());
		p.setUrl(sp.getUrl());
		p.setDeposit(sp.getDeposit());
		p.setWithdraw(sp.getWithdraw());
		
		p.getFees().setFlat(sp.getFees().getFlat());
		p.getFees().setMinimum(sp.getFees().getMinimum());
		p.getFees().setPercentage(sp.getFees().getPercentage());
		p.getFees().setStrategy(sp.getFees().getStrategy());
		
		p.getLimits().setMaxAmount(sp.getLimits().getMaxAmount());
		p.getLimits().setMaxAmountDay(sp.getLimits().getMaxAmountDay());
		p.getLimits().setMaxAmountMonth(sp.getLimits().getMaxAmountMonth());
		p.getLimits().setMaxAmountWeek(sp.getLimits().getMaxAmountWeek());
		p.getLimits().setMaxTransactionsDay(sp.getLimits().getMaxTransactionsDay());
		p.getLimits().setMaxTransactionsMonth(sp.getLimits().getMaxTransactionsMonth());
		p.getLimits().setMaxTransactionsWeek(sp.getLimits().getMaxTransactionsWeek());
		p.getLimits().setMinAmount(sp.getLimits().getMinAmount());
		p.getLimits().setMinFirstTransactionAmount(sp.getLimits().getMinFirstTransactionAmount());
		p.getLimits().setMaxFirstTransactionAmount(sp.getLimits().getMinFirstTransactionAmount());

		p.setFees(feesService.save(p.getFees()));
		p.setLimits(limitsService.save(p.getLimits()));
		p = processorService.save(p);
		
		for (lithium.service.cashier.client.objects.transaction.dto.Method sm: sp.getMethods()) processMethod(sm, p);
		for (lithium.service.cashier.client.objects.ProcessorProperty spp: sp.getProperties()) processProperty(spp, p);
	}
	
	private ProcessorProperty processProperty(lithium.service.cashier.client.objects.ProcessorProperty spp, Processor p) {
		ProcessorProperty pp = processorService.findPropertyByProcessorIdAndName(p, spp.getName());
		if (pp == null) pp = ProcessorProperty.builder().processor(p).name(spp.getName()).build();
		pp.setDefaultValue(spp.getDefaultValue());
		pp.setDescription((spp.getDescription().length()<=255)?spp.getDescription():spp.getDescription().substring(0, 255));
		pp.setType(spp.getType());
		pp.setAvailableForClient(spp.isAvalableForClient());
		pp = processorService.saveProperty(pp);
		return pp;
	}
	
	private Method processMethod(lithium.service.cashier.client.objects.transaction.dto.Method sm, Processor p) {
		log.info("processMethod " + sm.getCode() + " " + sm.getName());
		
		Method m = methodService.findByCode(sm.getCode());
		if (m == null) m = new Method();
		if (m.getImage() == null) m.setImage(new Image());
		
		if (sm.getProperties() == null) sm.setProperties(new ArrayList<>());
		for (lithium.service.cashier.client.objects.ProcessorProperty spp: sm.getProperties()) processProperty(spp, p);
		
		m.setCode(sm.getCode());
		m.setName(sm.getName());
		m.setInApp(sm.getInApp());
		m.setPlatform(sm.getPlatform());
		m.getImage().setBase64(sm.getImage().getBase64());
		m.getImage().setFilename(sm.getImage().getFilename());
		m.getImage().setFilesize(sm.getImage().getFilesize());
		m.getImage().setFiletype(sm.getImage().getFiletype());
		
		m.setImage(imageService.save(m.getImage()));
		m = methodService.save(m);
		
		ProcessorMethod pm = processorMethodService.findByProcessorAndMethod(p, m);
		if (pm == null) processorMethodService.save(ProcessorMethod.builder().processor(p).method(m).build());

		if (sm.getDepositStages() != null)
		for (lithium.service.cashier.client.objects.transaction.dto.MethodStage sms: sm.getDepositStages()) processMethodStage(sms, m, true);
		
		if (sm.getWithdrawalStages() != null)
		for (lithium.service.cashier.client.objects.transaction.dto.MethodStage sms: sm.getWithdrawalStages()) processMethodStage(sms, m, false);
		
		return m;
	}

	/**
	 * Update the database based on the contents of the JSON objects received on the queue.
	 * Take note that this will clobber the configuration as it is in the database. The json is the single source of truth.
	 */
	private MethodStage processMethodStage(lithium.service.cashier.client.objects.transaction.dto.MethodStage sms, Method m, boolean deposit) {
		MethodStage ms = stageService.findByMethodAndStageNumberAndDeposit(m, sms.getNumber(), deposit);
		if (ms == null) ms = MethodStage.builder().method(m).deposit(deposit).number(sms.getNumber()).build();

		ms.setDescription(sms.getDescription());
		ms.setTitle(sms.getTitle());
		ms = stageService.save(ms);

		//TODO This registers new fields, but does not remove deprecated fields / removed fields
		if (sms.getInputFields() != null)
		for (lithium.service.cashier.client.objects.transaction.dto.MethodStageField smsf: sms.getInputFields()) processMethodStageField(smsf, ms, true);

		if (sms.getOutputFields() != null)
		for (lithium.service.cashier.client.objects.transaction.dto.MethodStageField smsf: sms.getOutputFields()) processMethodStageField(smsf, ms, false);

		removeRedundantFields(sms, ms, true);
		removeRedundantFields(sms, ms, false);

		return ms;
	}

	private MethodStageField processMethodStageField(lithium.service.cashier.client.objects.transaction.dto.MethodStageField smsf, MethodStage ms, boolean input) {
		log.info("processMethodStageField " + smsf);
		MethodStageField msf = stageFieldService.findByCode(ms, input, smsf.getCode());
		if (msf == null) msf = MethodStageField.builder().code(smsf.getCode()).input(input).stage(ms).build();
		msf.setDescription(smsf.getDescription());
		msf.setDisplayOrder(smsf.getDisplayOrder());
		msf.setName(smsf.getName());
		if (smsf.getSizeMd() != null) msf.setSizeMd(smsf.getSizeMd());
		if (smsf.getSizeXs() != null) msf.setSizeXs(smsf.getSizeXs());
		if (smsf.getType() != null) msf.setType(smsf.getType());
		if (smsf.getId() == null) log.info("New field " + msf);
		if (smsf.getRequired() != null) msf.setRequired(smsf.getRequired());
		msf = stageFieldService.save(msf);
		return msf;
	}

	private void removeRedundantFields(lithium.service.cashier.client.objects.transaction.dto.MethodStage sms, MethodStage ms, boolean input) {
		log.info("removeRedundantFields " + sms);
		List<MethodStageField> fields = stageFieldService.findByMethodStageAndInput(ms, input);

		for (MethodStageField field : fields) {
			boolean found = false;
			lithium.service.cashier.client.objects.transaction.dto.MethodStageField[] newFields =
					input ? sms.getInputFields() : sms.getOutputFields();
			for (lithium.service.cashier.client.objects.transaction.dto.MethodStageField newField : newFields) {
				if (field.getCode().equals(newField.getCode())) found = true;
			}
			if (!found) {
				log.info("Field redundant. Removing. " + field);
				stageFieldService.delete(field);
			}
		}
	}

}
