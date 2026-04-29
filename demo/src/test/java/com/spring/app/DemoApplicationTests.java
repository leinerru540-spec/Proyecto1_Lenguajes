package com.spring.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.app.entity.Cliente;
import com.spring.app.entity.Consultoria;
import com.spring.app.repository.ClienteRepository;
import com.spring.app.repository.ConsultoriaRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AppApplication.class)
@AutoConfigureMockMvc
class DemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ConsultoriaRepository consultoriaRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void contextLoads() {
    }

    @Test
    void consultoriaViewsRender() throws Exception {
        String adminToken = loginAndGetToken("admin@demo.com", "admin123");
        MockCookie adminCookie = new MockCookie("jwtToken", adminToken);

        Consultoria consultoria = new Consultoria();
        consultoria.setTipo("Legal");
        consultoria.setDescripcion("Revision de contrato");
        consultoriaRepository.save(consultoria);

        mockMvc.perform(get("/vista/consultorias")
                        .cookie(adminCookie))
                .andExpect(status().isOk());

        mockMvc.perform(get("/vista/consultoria")
                        .cookie(adminCookie))
                .andExpect(status().isOk());

        mockMvc.perform(get("/vista/consultorias/nueva")
                        .cookie(adminCookie))
                .andExpect(status().isOk());

        mockMvc.perform(post("/vista/consultorias/nueva")
                        .cookie(adminCookie)
                        .param("tipo", "Ambiental")
                        .param("descripcion", "Gestion de permisos ambientales"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void apiRequiresJwtAndAppliesRoles() throws Exception {
        String adminToken = loginAndGetToken("admin@demo.com", "admin123");
        String clienteToken = loginAndGetToken("cliente@demo.com", "cliente123");

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/clientes")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/usuarios")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/clientes")
                        .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/usuarios")
                        .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/consultorias")
                        .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/consultorias")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tipo": "Ambiental",
                                  "descripcion": "Servicio ambiental"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/consultorias")
                        .header("Authorization", "Bearer " + clienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tipo": "Industrial",
                                  "descripcion": "Servicio industrial"
                                }
                                """))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/solicitudes")
                        .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isOk());
    }

    @Test
    void browserViewsAreProtectedByJwtCookie() throws Exception {
        String adminToken = loginAndGetToken("admin@demo.com", "admin123");
        String clienteToken = loginAndGetToken("cliente@demo.com", "cliente123");

        mockMvc.perform(get("/vista/clientes")
                        .cookie(new MockCookie("jwtToken", adminToken)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/vista/clientes")
                        .cookie(new MockCookie("jwtToken", clienteToken)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/vista/clientes"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/vista/solicitudes")
                        .cookie(new MockCookie("jwtToken", clienteToken)))
                .andExpect(status().isOk());
    }

    @Test
    void clienteCreatesPendingSolicitudAndAdminCanUpdateStatus() throws Exception {
        String adminToken = loginAndGetToken("admin@demo.com", "admin123");
        String clienteToken = loginAndGetToken("cliente@demo.com", "cliente123");

        Cliente cliente = new Cliente();
        cliente.setNombre("Cliente solicitud");
        cliente.setTelefono("7000-0000");
        cliente.setCorreo("cliente-solicitud@example.com");
        cliente = clienteRepository.save(cliente);

        Consultoria consultoria = new Consultoria();
        consultoria.setTipo("Legal");
        consultoria.setDescripcion("Servicio legal");
        consultoria = consultoriaRepository.save(consultoria);

        MvcResult createResult = mockMvc.perform(post("/solicitudes")
                        .header("Authorization", "Bearer " + clienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombreSolicitante": "Cliente",
                                  "correoSolicitante": "cliente@demo.com",
                                  "descripcion": "Necesito asesoria legal.",
                                  "estado": "FINALIZADA",
                                  "fecha": "2020-01-01",
                                  "consultoriaId": %d
                                }
                                """.formatted(consultoria.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andReturn();

        Long solicitudId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(put("/solicitudes/" + solicitudId)
                        .header("Authorization", "Bearer " + clienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombreSolicitante": "Cliente",
                                  "correoSolicitante": "cliente@demo.com",
                                  "descripcion": "Intento editar estado.",
                                  "estado": "FINALIZADA",
                                  "fecha": "2026-04-28",
                                  "consultoriaId": %d
                                }
                                """.formatted(consultoria.getId())))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/solicitudes/" + solicitudId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombreSolicitante": "Cliente",
                                  "correoSolicitante": "cliente@demo.com",
                                  "descripcion": "Solicitud en seguimiento.",
                                  "estado": "EN_PROCESO",
                                  "fecha": "2026-04-28",
                                  "clienteId": %d,
                                  "consultoriaId": %d
                                }
                                """.formatted(cliente.getId(), consultoria.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"))
                .andExpect(jsonPath("$.clienteId").value(cliente.getId()));
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(response);
        return json.get("token").asText();
    }
}
