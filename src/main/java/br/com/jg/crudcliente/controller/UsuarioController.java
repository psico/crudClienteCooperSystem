package br.com.jg.crudcliente.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import br.com.jg.crudcliente.entity.*;
import br.com.jg.crudcliente.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@CrossOrigin
@RestController
public class UsuarioController {

    @Autowired
    private UsuarioRepository _usuarioRepository;
    @Autowired
    private LogOperacoesRepository _logOperacoesRepository;
    @Autowired
    private EnderecoRepository _enderecoRepository;
    @Autowired
    private EmailRepository _emailRepository;
    @Autowired
    private TelefoneRepository _telefoneRepository;

    @RequestMapping(value = "/usuario", method = GET)
    public List<Usuario> Get() {
        return _usuarioRepository.findAll();
    }

    @RequestMapping(value = "/usuario/{id}", method = GET)
    public ResponseEntity<Usuario> GetById(@PathVariable(value = "id") long id) {
        Optional<Usuario> usuario = _usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/usuario", method = POST)
    public Usuario Post(@Valid @RequestBody Usuario usuario) {
        Usuario returnUsuario = _usuarioRepository.save(usuario);
        LogOperacoes logOperacoes = new LogOperacoes();
        logOperacoes.setTipoOperacao("Insert Usuario");
        logOperacoes.setData(new Date());
        logOperacoes.setIdUsuario(usuario.getIdUsuario());
        _logOperacoesRepository.save(logOperacoes);
        return returnUsuario;
    }

    @RequestMapping(value = "/usuario/{id}", method = PUT)
    public ResponseEntity<Usuario> Put(@PathVariable(value = "id") long id, @Valid @RequestBody Usuario newUsuario) {
        Optional<Usuario> oldUsuario = _usuarioRepository.findById(id);

        if (oldUsuario.isPresent()) {
            Usuario usuario = oldUsuario.get();
            usuario.setNome(newUsuario.getNome());
            _usuarioRepository.save(usuario);
            return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/usuario/{id}", method = DELETE)
    public ResponseEntity<Object> Delete(@PathVariable(value = "id") long id) {
        Optional<Usuario> usuario = _usuarioRepository.findById(id);

        if (usuario.isPresent()) {
            List<Email> emailList = _emailRepository.findByIdUsuario(usuario.get().getIdUsuario());
            emailList.forEach(email -> _emailRepository.delete(email));

            List<Endereco> enderecoList = _enderecoRepository.findByIdUsuario(usuario.get().getIdUsuario());
            enderecoList.forEach(endereco -> _enderecoRepository.delete(endereco));

            List<Telefone> telefoneList = _telefoneRepository.findByIdUsuario(usuario.get().getIdUsuario());
            telefoneList.forEach(telefone -> _telefoneRepository.delete(telefone));

            List<LogOperacoes> logOperacoesList = _logOperacoesRepository.findByIdUsuario(usuario.get().getIdUsuario());
            logOperacoesList.forEach(logOperacoe -> _logOperacoesRepository.delete(logOperacoe));

//            LogOperacoes logOperacoes = new LogOperacoes("Delete Usuario " + usuario.get().getIdUsuario(), 1);
//            _logOperacoesRepository.save(logOperacoes);

            _usuarioRepository.delete(usuario.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/usuario/endereco/{id}", method = GET)
    public ResponseEntity<Endereco> GetEnderecoById(@PathVariable(value = "id") long id) {
        Optional<Endereco> endereco = _enderecoRepository.findById(id);
        if (endereco.isPresent()) {
            return new ResponseEntity<Endereco>(endereco.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/usuario/telefone/{id}", method = GET)
    public List<Telefone> GetTelefonesById(@PathVariable(value = "id") long id) {
        Optional<Usuario> usuario = _usuarioRepository.findById(id);
        return _telefoneRepository.findByIdUsuario(usuario.get().getIdUsuario());
    }

    @RequestMapping(value = "/usuario/email/{id}", method = GET)
    public List<Email> GetEmailsById(@PathVariable(value = "id") long id) {
        Optional<Usuario> usuario = _usuarioRepository.findById(id);
        return _emailRepository.findByIdUsuario(usuario.get().getIdUsuario());
    }
}
