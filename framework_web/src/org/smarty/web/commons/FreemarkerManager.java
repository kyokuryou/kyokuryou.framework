package org.smarty.web.commons;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.smarty.core.logger.RuntimeLogger;
import org.smarty.core.utils.LogicUtil;
import org.smarty.web.utils.SpringWebUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * Freemarker
 * Created Date 2015/04/09
 *
 * @author quliang
 * @version 1.0
 */
public class FreemarkerManager implements InitializingBean {
    private static RuntimeLogger logger = new RuntimeLogger(FreemarkerManager.class);
    private Configuration configuration;

    public FreemarkerManager() {
    }

    public void afterPropertiesSet() throws Exception {

    }

    public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        if (freeMarkerConfigurer == null) {
            return;
        }
        this.configuration = freeMarkerConfigurer.getConfiguration();
    }

    public void outputTemplate(String name, Map<String, Object> data, OutputStream os) throws IOException {
        if (LogicUtil.isEmpty(name) || os == null) {
            return;
        }
        try {
            OutputStreamWriter sw = new OutputStreamWriter(os);
            Template template = configuration.getTemplate(name);
            template.process(SpringWebUtil.getCommonData(data), sw);
            sw.flush();
        } catch (TemplateException e) {
            logger.out(e);
        } catch (IOException e) {
            logger.out(e);
            throw e;
        }
    }

    public String getTemplateString(String name, Map<String, Object> data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        outputTemplate(name, data, baos);
        return baos.toString();
    }
}