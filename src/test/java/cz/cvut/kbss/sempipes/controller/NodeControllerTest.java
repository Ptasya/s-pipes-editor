package cz.cvut.kbss.sempipes.controller;

import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import cz.cvut.kbss.jsonld.deserialization.JsonLdDeserializer;
import cz.cvut.kbss.sempipes.config.RestConfig;
import cz.cvut.kbss.sempipes.dao.GraphDao;
import cz.cvut.kbss.sempipes.model.graph.Node;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Miroslav Blasko on 18.11.16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RestConfig.class})
@WebAppConfiguration
public class NodeControllerTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    MockMvc mockMvc;

    private JsonLdDeserializer deserializer;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        deserializer = JsonLdDeserializer.createExpandedDeserializer();
    }


    @Test
    public void getNode() throws Exception {
        //andExpect(status().isOk())
        MvcResult result = mockMvc.perform(get("/test/node")).andReturn();
        String jsonLd = result.getResponse().getContentAsString();

        Object jsonLdObject = JsonUtils.fromString(jsonLd);
        List<Object> expanded = JsonLdProcessor.expand(jsonLdObject);
        deserializer.deserialize(expanded, Node.class);
    }

}