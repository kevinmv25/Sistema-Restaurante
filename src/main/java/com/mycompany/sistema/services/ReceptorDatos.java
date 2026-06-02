
package com.mycompany.sistema.services;

/**
 * Define un contrato para pantallas que necesitan recibir datos al cargarse.
 *
 * <p>Esta interfaz permite que una pantalla abierta mediante
 * <code>SceneService</code> reciba un objeto proveniente de otra ventana. Es útil
 * cuando un módulo externo, como pedidos o mesas, necesita enviar información al
 * flujo de cajero.</p>
 *
 * @author Gutierrez Colorado Oliver
 * @see SceneService
 */
public interface  ReceptorDatos {
    /**
    * Recibe información enviada desde otra pantalla.
    *
    * <p>La clase que implemente este método debe validar el tipo de objeto recibido
    * antes de usarlo, para evitar errores de conversión.</p>
    *
    * @param datos objeto enviado desde la pantalla anterior.
    */
    void recibirDatos(Object datos);
}
