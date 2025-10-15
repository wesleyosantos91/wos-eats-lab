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
  PERFORMANCE_THRESHOLDS
} from '../utils/api-utils.js';

import { sleep } from 'k6';

export let options = {
  stages: [
    { duration: '1m', target: 20 },   // Ramp up to simulate realistic user load
    { duration: '10m', target: 20 },  // Maintain load for synthetic monitoring
    { duration: '1m', target: 0 },    // Ramp down
  ],
  thresholds: {
    ...PERFORMANCE_THRESHOLDS.load,
    // Stricter thresholds for synthetic monitoring
    http_req_duration: ['p(95)<800'],  // 95% under 800ms
    http_req_failed: ['rate<0.05'],    // Error rate under 5%
  },
};

let userJourneyCounter = 0;

export function setup() {
  console.log('Starting Synthetic Test Setup...');
  console.log('This test simulates realistic user journeys and business workflows');
  
  return {
    testStartTime: Date.now()
  };
}

export default function(data) {
  const journeyType = Math.random();
  
  if (journeyType < 0.3) {
    // 30% - Restaurant Owner Journey
    restaurantOwnerJourney();
  }
  else if (journeyType < 0.6) {
    // 30% - Food Delivery Manager Journey  
    deliveryManagerJourney();
  }
  else if (journeyType < 0.85) {
    // 25% - Admin/Support Journey
    adminSupportJourney();
  }
  else {
    // 15% - Customer Discovery Journey
    customerDiscoveryJourney();
  }
}

function restaurantOwnerJourney() {
  const journeyStart = Date.now();
  console.log('Starting Restaurant Owner Journey...');
  
  // 1. Owner logs in and checks their restaurant listings
  getAllRestaurants();
  sleep(1); // Simulate reading time
  
  // 2. Owner creates a new restaurant
  const restaurantData = { name: "Owner Restaurant " + Date.now(), deliveryFee: Math.random() * 5 + 3 };
  const newRestaurantName = restaurantData.name + ' - New Branch ' + Date.now();
  createRestaurant({ 
    ...restaurantData, 
    name: newRestaurantName 
  });
  sleep(0.5);
  
  // 3. Owner verifies creation and checks updated list
  getAllRestaurants();
  sleep(1);
  
  // 4. Owner also checks available kitchens for partnerships
  getAllKitchens();
  sleep(1);
  
  // 5. Owner updates restaurant info (menu update, price change)
  const updateId = Math.floor(Math.random() * 10) + 1;
  updateRestaurant(updateId, {
    ...restaurantData,
    name: restaurantData.name + ' - Menu Updated',
    deliveryFee: restaurantData.deliveryFee + 0.5
  });
  
  const journeyDuration = Date.now() - journeyStart;
  console.log(`Restaurant Owner Journey completed in ${journeyDuration}ms`);
}

function deliveryManagerJourney() {
  const journeyStart = Date.now();
  console.log('Starting Delivery Manager Journey...');
  
  // 1. Manager checks all restaurants for delivery route planning
  getAllRestaurants();
  sleep(0.5);
  
  // 2. Manager checks specific restaurants for delivery fee analysis
  for (let i = 1; i <= 5; i++) {
    getRestaurant(i);
    sleep(0.2); // Quick scanning
  }
  
  // 3. Manager checks kitchen locations for logistics
  getAllKitchens();
  sleep(0.5);
  
  // 4. Manager checks specific kitchens for capacity planning
  for (let i = 1; i <= 3; i++) {
    getKitchen(i);
    sleep(0.2);
  }
  
  // 5. Manager creates new delivery kitchen hub
  const kitchenData = { name: "Hub Kitchen " + Date.now() };
  createKitchen({
    ...kitchenData,
    name: kitchenData.name + ' - Delivery Hub ' + Date.now()
  });
  
  const journeyDuration = Date.now() - journeyStart;
  console.log(`Delivery Manager Journey completed in ${journeyDuration}ms`);
}

function adminSupportJourney() {
  const journeyStart = Date.now();
  console.log('Starting Admin/Support Journey...');
  
  // 1. Admin reviews system status by checking all entities
  getAllKitchens();
  getAllRestaurants();
  sleep(1); // Admin review time
  
  // 2. Admin handles support ticket - checks specific restaurant
  const restaurantId = Math.floor(Math.random() * 15) + 1;
  getRestaurant(restaurantId);
  sleep(0.5);
  
  // 3. Admin updates restaurant info to fix reported issue
  const restaurantData = { name: "Fixed Restaurant " + Date.now(), deliveryFee: Math.round(Math.random() * 500) / 100 };
  updateRestaurant(restaurantId, {
    ...restaurantData,
    name: restaurantData.name + ' - Issue Fixed',
    deliveryFee: Math.round(restaurantData.deliveryFee * 100) / 100 // Fix decimal precision
  });
  sleep(0.5);
  
  // 4. Admin checks kitchen for compliance review
  const kitchenId = Math.floor(Math.random() * 15) + 1;
  getKitchen(kitchenId);
  sleep(0.5);
  
  // 5. Admin creates audit trail kitchen entry
  const kitchenData = { name: "Audit Kitchen " + Date.now() };
  createKitchen({
    ...kitchenData,
    name: 'Audit - ' + kitchenData.name + ' - ' + Date.now()
  });
  
  // 6. Admin verifies final state
  getAllRestaurants();
  
  const journeyDuration = Date.now() - journeyStart;
  console.log(`Admin/Support Journey completed in ${journeyDuration}ms`);
}

function customerDiscoveryJourney() {
  const journeyStart = Date.now();
  console.log('Starting Customer Discovery Journey...');
  
  // 1. Customer browses available restaurants
  getAllRestaurants();
  sleep(2); // Customer browsing time
  
  // 2. Customer checks specific restaurants based on preferences
  const restaurantIds = [1, 2, 3, 5, 8]; // Simulate user clicking on interesting ones
  restaurantIds.forEach(id => {
    getRestaurant(id);
    sleep(0.8); // Customer reading restaurant details
  });
  
  // 3. Customer also checks kitchen types for dietary preferences
  getAllKitchens();
  sleep(1);
  
  // 4. Customer checks specific kitchens
  const kitchenIds = [1, 3, 5];
  kitchenIds.forEach(id => {
    getKitchen(id);
    sleep(0.5); // Customer reviewing kitchen info
  });
  
  // 5. Customer goes back to restaurants for final decision
  getAllRestaurants();
  sleep(1);
  
  const journeyDuration = Date.now() - journeyStart;
  console.log(`Customer Discovery Journey completed in ${journeyDuration}ms`);
}

export function teardown(data) {
  const testDuration = (Date.now() - data.testStartTime) / 1000;
  console.log(`Synthetic Test completed in ${testDuration} seconds`);
  console.log('Summary: Tested realistic user journeys and business workflows');
  console.log('- Restaurant Owner Journey: Create, update, manage restaurants');
  console.log('- Delivery Manager Journey: Route planning, logistics, hub management');
  console.log('- Admin/Support Journey: Issue resolution, compliance, audit trails');
  console.log('- Customer Discovery Journey: Browse, compare, decision making');
}