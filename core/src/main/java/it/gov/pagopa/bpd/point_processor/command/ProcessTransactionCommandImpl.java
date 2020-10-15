package it.gov.pagopa.bpd.point_processor.command;

import eu.sia.meda.core.command.Command;
import it.gov.pagopa.bpd.point_processor.command.model.ProcessTransactionCommandModel;
import it.gov.pagopa.bpd.point_processor.mapper.TransactionMapper;
import it.gov.pagopa.bpd.point_processor.publisher.SaveTransactionPublisherConnector;
import it.gov.pagopa.bpd.point_processor.service.AwardPeriodConnectorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ProcessTransactionCommandImpl extends BaseProcessTransactionCommandImpl implements Command<Boolean> {

    public ProcessTransactionCommandImpl(ProcessTransactionCommandModel processTransactionCommandModel) {
        super(processTransactionCommandModel);
    }

    public ProcessTransactionCommandImpl(ProcessTransactionCommandModel processTransactionCommandModel,
                                         SaveTransactionPublisherConnector saveTransactionPublisherConnector,
                                         AwardPeriodConnectorService awardPeriodConnectorService,
                                         BeanFactory beanFactory,
                                         TransactionMapper transactionMapper) {
        super(processTransactionCommandModel,
                saveTransactionPublisherConnector,
                awardPeriodConnectorService,
                beanFactory,
                transactionMapper);
    }

    @Override
    public Boolean doExecute() {
        return super.doExecute();
    }

}
