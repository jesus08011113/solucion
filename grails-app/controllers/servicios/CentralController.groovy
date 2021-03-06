package servicios

import org.springframework.dao.DataIntegrityViolationException

class CentralController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 30, 100)
        [centralInstanceList: Central.list(params), centralInstanceTotal: Central.count()]
    }

    def create() {
        [centralInstance: new Central(params)]
    }

    def save() {
        def centralInstance = new Central(params)
        if (!centralInstance.save(flush: true)) {
            render(view: "create", model: [centralInstance: centralInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'central.label', default: 'Central'), centralInstance.id])
        redirect(action: "show", id: centralInstance.id)
    }

    def show(Long id) {
        def centralInstance = Central.get(id)
        if (!centralInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'central.label', default: 'Central'), id])
            redirect(action: "list")
            return
        }

        [centralInstance: centralInstance]
    }

    def edit(Long id) {
        def centralInstance = Central.get(id)
        if (!centralInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'central.label', default: 'Central'), id])
            redirect(action: "list")
            return
        }

        [centralInstance: centralInstance]
    }

    def update(Long id, Long version) {
        def centralInstance = Central.get(id)
        if (!centralInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'central.label', default: 'Central'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (centralInstance.version > version) {
                centralInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'central.label', default: 'Central')] as Object[],
                          "Another user has updated this Central while you were editing")
                render(view: "edit", model: [centralInstance: centralInstance])
                return
            }
        }

        centralInstance.properties = params

        if (!centralInstance.save(flush: true)) {
            render(view: "edit", model: [centralInstance: centralInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'central.label', default: 'Central'), centralInstance.id])
        redirect(action: "show", id: centralInstance.id)
    }

    def delete(Long id) {
        def centralInstance = Central.get(id)
        if (!centralInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'central.label', default: 'Central'), id])
            redirect(action: "list")
            return
        }

        try {
            centralInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'central.label', default: 'Central'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'central.label', default: 'Central'), id])
            redirect(action: "show", id: id)
        }
    }
    

}
