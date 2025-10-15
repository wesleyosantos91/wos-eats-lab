import { 
  getAllKitchens, 
  getAllRestaurants, 
  createKitchen, 
  createRestaurant, 
  getKitchen, 
  getRestaurant,
  updateKitchen,
  updateRestaurant,
  LOAD_PROFILES,
  PERFORMANCE_THRESHOLDS
} from '../utils/api-utils.js';

export let options = {
  stages: LOAD_PROFILES.spike.stages,
  thresholds: PERFORMANCE_THRESHOLDS.spike,
};

export function setup() {
  console.log('Starting Spike Test Setup...');
  console.log('This test will create sudden load spikes to test system resilience');
  
  return {
    testStartTime: Date.now()
  };
}

export default function(data) {
  // Spike test - simulate sudden traffic surge (e.g., flash sale, viral content)
  const spikeOperation = Math.random();
  
  if (spikeOperation < 0.4) {
    // 40% - Concurrent read operations (users browsing during spike)
    getAllKitchens();
    getAllRestaurants();
    
    // Simulate users rapidly clicking through listings
    for (let i = 1; i <= 5; i++) {
      const kitchenId = Math.floor(Math.random() * 30) + 1;
      const restaurantId = Math.floor(Math.random() * 30) + 1;
      getKitchen(kitchenId);
      getRestaurant(restaurantId);
    }
  }
  else if (spikeOperation < 0.7) {
    // 30% - Create operations (new signups during spike)
    const kitchenData = { name: "Spike Kitchen " + Date.now() };
    const restaurantData = { name: "Spike Restaurant " + Date.now(), deliveryFee: Math.random() * 5 + 3 };
    
    // Rapid creation attempts
    createKitchen({ ...kitchenData, name: kitchenData.name + ' - Spike ' + Date.now() });
    createRestaurant({ ...restaurantData, name: restaurantData.name + ' - Spike ' + Date.now() });
    
    // Immediate verification reads
    getAllKitchens();
    getAllRestaurants();
  }
  else if (spikeOperation < 0.9) {
    // 20% - Update operations (promotions, price changes during spike)
    const updateId = Math.floor(Math.random() * 20) + 1;
    
    const kitchenData = { name: "Promo Kitchen " + Date.now() };
    const restaurantData = { name: "Flash Restaurant " + Date.now(), deliveryFee: Math.random() * 3 + 2 };
    
    // Simulate promotional updates
    updateKitchen(updateId, { 
      ...kitchenData, 
      name: kitchenData.name + ' - PROMO' 
    });
    
    updateRestaurant(updateId, { 
      ...restaurantData, 
      name: restaurantData.name + ' - FLASH SALE',
      deliveryFee: Math.max(0.99, restaurantData.deliveryFee - 2.0) // Discount
    });
  }
  else {
    // 10% - Mixed high-intensity operations
    // Simulate power users doing multiple operations rapidly
    
    // Browse
    getAllKitchens();
    getAllRestaurants();
    
    // Create
    const kitchenData = { name: "Power Kitchen " + Date.now() };
    createKitchen(kitchenData);
    
    // Read specific items
    getKitchen(1);
    getKitchen(2);
    getRestaurant(1);
    getRestaurant(2);
    
    // Create another
    const restaurantData = { name: "Power Restaurant " + Date.now(), deliveryFee: Math.random() * 5 + 3 };
    createRestaurant(restaurantData);
    
    // Final browse
    getAllKitchens();
  }
}

export function teardown(data) {
  const testDuration = (Date.now() - data.testStartTime) / 1000;
  console.log(`Spike Test completed in ${testDuration} seconds`);
  console.log('Summary: Tested system resilience under sudden traffic spikes');
  console.log('Check metrics for recovery time and error patterns during spike');
}