/**
 * 
 */
package trafficProva;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Factory che produce le entità
 * @author vava5
 */
public class TrafficFactory implements EntityFactory {
	
	/**
	 * Metodo che spawna gli incroci
	 * @param data
	 * @return Entity
	 */
	@Spawns("Incrocio")
	public Entity incrocio(SpawnData data) 
	{
		return entityBuilder(data)
				.type(TrafficType.CROSSES)
				.bbox(new HitBox(BoundingShape.box(250,250)))
				.with(new CollidableComponent(true))
				.with(new CrossComponent())
				.build();

	}
	/**
	 * Metodo che spawna i semafori rossi
	 * @param data
	 * @return Entity
	 */
	@Spawns("SemaforoRosso")
	public Entity semaforoRosso(SpawnData data) 
	{
		Texture semRTexture = texture("gioco-14.png");
		return entityBuilder(data)
				.type(TrafficType.REDLIGHTS)
				.view(semRTexture)
				.scale(1,1)
				.build();
	}
	/**
	 * Metodo che spawna i semafori verdi
	 * @param data
	 * @return Entity
	 */
	@Spawns("SemaforoVerde")
	public Entity semaforoVerde(SpawnData data) 
	{
		Texture semVTexture = texture("gioco-10.png");
		return entityBuilder(data)
				.type(TrafficType.GREENLIGHTS)
				.view(semVTexture)
				.scale(1,1)
				.build();

	}
	/**
	 * Metodo che spawna gli incroci
	 * @param data
	 * @return
	 */
	@Spawns("Puntatore")
	public Entity puntatore(SpawnData data)
	{
		 Texture playerTexture = texture("Puntatore.png");
		 
		 return entityBuilder(data)
				 .type(TrafficType.PLAYER)
				 .view(playerTexture)
				 .scale(1,1)
				 .build();
	}
	/**
	 * Metodo che spawna le macchine
	 * @param data
	 * @return Entity
	 */
	@Spawns("Macchina")
	public Entity macchina(SpawnData data)
	{
		Texture textureMacchina = texture("Macchina.png");
		return entityBuilder(data)
				 .type(TrafficType.CARS)
				 .view(textureMacchina)
				 .bbox(new HitBox(BoundingShape.box(100,40)))
				 .scale(1,1)
				 .with(new CollidableComponent(true))
				 .with(new CarComponent())
				 .build();
	}
	/**
	 * Metodo che spawna le moto
	 * @param data
	 * @return Entity
	 */
	@Spawns("Moto")
	public Entity moto(SpawnData data)
	{
		Texture textureMoto = texture("Motorino.png");
		return entityBuilder(data)
				 .type(TrafficType.CARS)
				 .view(textureMoto)
				 .scale(2,2)
				 .bbox(new HitBox(BoundingShape.box(50,20)))
				 .with(new CollidableComponent(true))
				 .with(new CarComponent())
				 .build();
	}
	/**
	 * Metodo che spawna i camion
	 * @param data
	 * @return Entity
	 */
	@Spawns("Camion")
	public Entity camion(SpawnData data)
	{
		Texture textureCamion = texture("Camion.png");
		return entityBuilder(data)
				 .type(TrafficType.CARS)
				 .view(textureCamion)
				 .bbox(new HitBox(BoundingShape.box(130,40)))
				 .scale(1,1)
				 .with(new CollidableComponent(true))
				 .with(new CarComponent())
				 .build();
	}
	/**
	 * Metodo che spawna gli spawner di veicoli
	 * @param data
	 * @return Entity
	 */
	@Spawns("VehicleSpawner")
	public Entity vehicleSpawner(SpawnData data) 
	{
		return entityBuilder(data)
				.type(TrafficType.VEHICLESPAWNERS)
				.with(new VehicleSpawnerComponent())
				.build();

	}
	/**
	 * Metodo che spawna le uscite
	 * @param data
	 * @return Entity
	 */
	@Spawns("Uscita")
	public Entity exitSpawner(SpawnData data) 
	{
		return entityBuilder(data)
				.type(TrafficType.EXITS)
				.bbox(new HitBox(BoundingShape.box(250,250)))
				.with(new CollidableComponent(true))
				.build();

	}
	/**
	 * Metodo che spawna le zone fuoristrada
	 * @param data
	 * @return Entity
	 */
	@Spawns("FuoriStrada")
	public Entity fuoriStrda(SpawnData data) 
	{
		return entityBuilder(data)
				.type(TrafficType.OUTOFRAILS)
				.bbox(new HitBox(BoundingShape.box((double)Double.valueOf(String.valueOf(data.getData().get("width"))), (double)Double.valueOf(String.valueOf(data.getData().get("height"))))))
				.with(new CollidableComponent(true))
				.build();

	}

}
