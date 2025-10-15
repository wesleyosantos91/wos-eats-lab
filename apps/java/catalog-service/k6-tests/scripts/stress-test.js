import { 
  getAllKitchens, 
  getAllRestaurants, 
  createKitchen, 
  createRestaurant, 
  getKitchen, 
  getRestaurant,
  updateKitchen,
  updateRestaurant,
  deleteKitchen,
  deleteRestaurant,
  LOAD_PROFILES,
  PERFORMANCE_THRESHOLDS
} from '../utils/api-utils.js';

export let options = {
  stages: LOAD_PROFILES.stress.stages,
  thresholds: PERFORMANCE_THRESHOLDS.stress,
};

let createdKitchenIds = [];
let createdRestaurantIds = [];

export function setup() {
  console.log('Starting Stress Test Setup...');
  console.log('This test will gradually increase load to find system breaking points');
  
  return {
    testStartTime: Date.now()
  };
}

export default function(data) {
  // More aggressive testing patterns for stress test
  const operationType = Math.random();
  
  if (operationType < 0.2) {
    // 20% - Heavy read operations
    getAllKitchens();
    getAllRestaurants();
    
    // Additional concurrent reads
    const id1 = Math.floor(Math.random() * 20) + 1;
    const id2 = Math.floor(Math.random() * 20) + 1;
    getKitchen(id1);
    getRestaurant(id2);
  }
  else if (operationType < 0.4) {
    // 20% - Kitchen CRUD operations
    const kitchenData = { name: "Stress Kitchen " + Date.now() };
    
    // Create
    const createResult = createKitchen(kitchenData);
    if (createResult) {
      // Simulate immediate read after create
      const newId = Math.floor(Math.random() * 50) + 1; // Simulate getting new ID
      getKitchen(newId);
      
      // Update
      const updatedData = { ...kitchenData, name: kitchenData.name + ' - Updated' };
      updateKitchen(newId, updatedData);
    }
  }
  else if (operationType < 0.6) {
    // 20% - Restaurant CRUD operations  
    const restaurantData = { name: "Stress Restaurant " + Date.now(), deliveryFee: Math.random() * 5 + 3 };
    
    // Create
    const createResult = createRestaurant(restaurantData);
    if (createResult) {
      // Simulate immediate read after create
      const newId = Math.floor(Math.random() * 50) + 1; // Simulate getting new ID
      getRestaurant(newId);
      
      // Update
      const updatedData = { 
        ...restaurantData, 
        name: restaurantData.name + ' - Stressed',
        deliveryFee: restaurantData.deliveryFee + 1.0 
      };
      updateRestaurant(newId, updatedData);
    }
  }
  else if (operationType < 0.8) {
    // 20% - Mixed rapid operations
    getAllKitchens();
    
    const kitchenData = { name: "Mixed Kitchen " + Date.now() };
    createKitchen(kitchenData);
    
    getAllRestaurants();
    
    const restaurantData = { name: "Mixed Restaurant " + Date.now(), deliveryFee: Math.random() * 5 + 3 };
    createRestaurant(restaurantData);
    
    // Rapid fire reads
    for (let i = 1; i <= 3; i++) {
      getKitchen(i);
      getRestaurant(i);
    }
  }
  else {
    // 20% - Delete operations (simulate cleanup/admin operations under stress)
    const deleteId = Math.floor(Math.random() * 20) + 1;
    
    // Try to delete (may fail if not exists, which is fine for stress test)
    deleteKitchen(deleteId);
    deleteRestaurant(deleteId);
    
    // Immediately recreate to maintain data
    const kitchenData = { name: "Recreated Kitchen " + Date.now() };
    const restaurantData = { name: "Recreated Restaurant " + Date.now(), deliveryFee: Math.random() * 5 + 3 };
    createKitchen(kitchenData);
    createRestaurant(restaurantData);
  }
}

export function teardown(data) {
  const testDuration = (Date.now() - data.testStartTime) / 1000;
  console.log(`Stress Test completed in ${testDuration} seconds`);
  console.log('Summary: Tested system under increasing load with aggressive CRUD operations');
  console.log('Check metrics for breaking points and degradation patterns');
}