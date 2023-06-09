package sk.stuba.fei.uim.oop.animal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.fei.uim.oop.exceptions.NotFoundException;
import sk.stuba.fei.uim.oop.payment.IPaymentService;
import sk.stuba.fei.uim.oop.payment.Payment;
import sk.stuba.fei.uim.oop.person.IPersonService;
import sk.stuba.fei.uim.oop.person.Person;

import java.util.List;
import java.util.Optional;

@Service
public class AnimalService implements IAnimalService {

    private IAnimalRepository repository;

    private IPersonService personService;

    private IPaymentService paymentService;

    public AnimalService(IAnimalRepository repository, IPersonService personService, IPaymentService paymentService) {
        this.repository = repository;
        this.personService = personService;
        this.paymentService = paymentService;
        Animal a1 = new Animal("dog", "luna");
        Animal a2 = new Animal("cat", "murko");
        this.repository.save(a1);
        System.out.println(a2);
        this.repository.save(a2);
        System.out.println(a2);
        this.repository.delete(a2);
    }

    public List<Animal> getAll() {
        return this.repository.findAll();
    }

    public Animal createAnimal(AnimalRequest body) {
        Animal a = new Animal(body.getSpecies(), body.getName());
        return this.repository.save(a);
    }

    @Override
    public List<Animal> getByName(String name) {
        return this.repository.findAllByName(name);
    }

    @Override
    public Animal addPersonToAnimal(Long id, Long personId) throws NotFoundException {
        Optional<Animal> animalOpt = this.repository.findById(id);
        Animal animal = animalOpt.orElseThrow(NotFoundException::new);
        Person person = this.personService.getById(personId);

        Payment pay = new Payment(animal, person, 10);
        pay = this.paymentService.save(pay);

        animal.getPayments().add(pay);
        animal = this.repository.save(animal);

        person.getPayments().add(pay);
        this.personService.save(person);

        return animal;
    }
}
