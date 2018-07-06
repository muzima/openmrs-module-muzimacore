package org.openmrs.module.muzima.handler;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.muzima.exception.QueueProcessorException;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.model.handler.QueueDataHandler;
import org.springframework.stereotype.Component;

@Component
@Handler(supports = QueueData.class, order = 7)
public class ReportTemplateDummyHandler implements QueueDataHandler {
    public static final String DISCRIMINATOR_VALUE = "provider-report";
    private final Log log = LogFactory.getLog(ReportTemplateDummyHandler.class);

    /**
     *
     * @param queueData - QueueData
     * @throws QueueProcessorException
     */
    @Override
    public void process(final QueueData queueData) throws QueueProcessorException {
        log.info("Processing encounter form data: " + queueData.getUuid());
    }

    /**
     *
     * @param queueData - QueueData
     * @return boolean
     */
    @Override
    public boolean accept(final QueueData queueData) {
        return StringUtils.equals(DISCRIMINATOR_VALUE, queueData.getDiscriminator());
    }

    /**
     *
     * @param queueData - QueueDate
     * @return boolean
     */
    @Override
    public boolean validate(QueueData queueData) {
        return false;
    }

    /**
     *
     * @return String
     */
    @Override
    public String getDiscriminator() {
        return DISCRIMINATOR_VALUE;
    }
}
