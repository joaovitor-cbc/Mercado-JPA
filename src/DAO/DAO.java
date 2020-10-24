/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * @author joao_vitor
 */
public class DAO<E> {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private Class<E> classe;

    static {
        try {
            emf = Persistence.createEntityManagerFactory("projeto-mercado");
        } catch (Exception e) {
            System.out.println("Erro de " + e.getMessage());
        }
    }

    private DAO() {
        this(null);
    }

    public DAO(Class<E> classe) {
        this.classe = classe;
        em = emf.createEntityManager();
    }

    public DAO<E> abrirTransacao() {
        em.getTransaction().begin();
        return this;
    }

    public DAO<E> fecharTransacao() {
        em.getTransaction().commit();
        return this;
    }

    public E obterPorID(Long id) {
        return em.find(classe, id);
    }

    public DAO<E> incluir(E entidade) {
        em.persist(entidade);
        return this;
    }

    public DAO<E> incluirAtomico(E entidade) {
        return this.abrirTransacao().incluir(entidade).fecharTransacao();
    }

    public DAO<E> alterar(E entidade) {
        abrirTransacao();
        em.merge(entidade);
        fecharTransacao();
        return this;
    }

    public DAO<E> excluir(E entidade) {
        abrirTransacao();
        em.remove(entidade);
        fecharTransacao();
        return this;
    }

    public List<E> listaTodos() {
        return this.listaTodos(10, 0);
    }

    public List<E> listaTodos(int qtd, int deslocamento) {
        if (classe == null) {
            throw new UnsupportedOperationException("Classe nula.");
        }
        String jpql = "select e from " + classe.getName() + " e";
        TypedQuery<E> query = em.createQuery(jpql, classe);
        query.setMaxResults(qtd);
        query.setFirstResult(deslocamento);
        return query.getResultList();
    }
        public List<E> listaCompleta() {
        if (classe == null) {
            throw new UnsupportedOperationException("Classe nula.");
        }
        String jpql = "select e from " + classe.getName() + " e";
        TypedQuery<E> query = em.createQuery(jpql, classe);
        return query.getResultList();
    }

    public void fechar() {
        em.close();
    }

    public List<E> consulta(String nomeConsulta, Object... params) {
        TypedQuery<E> query = em.createNamedQuery(nomeConsulta, classe);
        for (int i = 0; i < params.length; i += 2) {
            query.setParameter(params[i].toString(), params[i + 1]);
        }
        return query.getResultList();
    }

    public E consultaUm(String nomeConsulta, Object... params) {
        List<E> lista = consulta(nomeConsulta, params);
        return lista.isEmpty() ? null : lista.get(0);
    }
}
