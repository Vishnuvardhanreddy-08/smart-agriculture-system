// cropNames.js: shows a crop's name in the farmer's current language.
//
// Crop names are typed by the farmer and saved exactly as typed (for example "Rice" or "వరి").
// This file recognises the common crops, in English OR Telugu, and shows them in the CURRENT
// language. A name we do not recognise (for example a local variety like "BPT-5204")
// is shown exactly as typed.

import i18n from '../i18n.js'
import enCrops from '../locales/en/crops.json'
import teCrops from '../locales/te/crops.json'

// Other common spellings that mean the same crop.
// The left side is the other spelling (lower case for English), the right side is the crop's key.
const ALIASES = {
    chili: 'Chilli',
    chilly: 'Chilli',
    chillies: 'Chilli',
    మిర్చి: 'Chilli',
    మిరపకాయ: 'Chilli',
    paddy: 'Rice',
    ధాన్యం: 'Rice',
    corn: 'Maize',
    మొక్కజొన్నలు: 'Maize',
    peanut: 'Groundnut',
    peanuts: 'Groundnut',
    వేరుశెనగ: 'Groundnut',
    పల్లీలు: 'Groundnut',
    'sugar cane': 'Sugarcane',
    'pigeon pea': 'Red gram',
    pigeonpea: 'Red gram',
    tur: 'Red gram',
    toor: 'Red gram',
    కందులు: 'Red gram',
    moong: 'Green gram',
    mung: 'Green gram',
    పెసలు: 'Green gram',
    urad: 'Black gram',
    మినుములు: 'Black gram',
    chickpea: 'Bengal gram',
    chana: 'Bengal gram',
    gram: 'Bengal gram',
    శనగలు: 'Bengal gram',
    sorghum: 'Jowar',
    'pearl millet': 'Bajra',
    'finger millet': 'Ragi',
    రాగులు: 'Ragi',
    'ladies finger': 'Okra',
    'lady finger': 'Okra',
    bhendi: 'Okra',
    eggplant: 'Brinjal',
    టమాటో: 'Tomato',
    టమోటా: 'Tomato',
}

// Cleans a name so that "  Rice ", "rice" and "RICE" all look the same.
function normalize(text) {
    return String(text).trim().toLowerCase().replace(/\s+/g, ' ')
}

// The lookup table: every known spelling (English, Telugu, alias) -> the crop's key.
const LOOKUP = new Map()

for (const key of Object.keys(enCrops.names)) {
    LOOKUP.set(normalize(key), key)
    LOOKUP.set(normalize(enCrops.names[key]), key)
    LOOKUP.set(normalize(teCrops.names[key]), key)
}

for (const [alias, key] of Object.entries(ALIASES)) {
    LOOKUP.set(normalize(alias), key)
}

// The name to SHOW for a saved crop name.
//   "Rice"  -> "Rice" in English, "వరి" in Telugu
//   "వరి"   -> "Rice" in English, "వరి" in Telugu
//   "BPT-5204" (not in our list) -> "BPT-5204" always
export function cropLabel(name) {
    if (!name) {
        return ''
    }
    const key = LOOKUP.get(normalize(name))
    return key ? i18n.t(`crops:names.${key}`) : name
}

// The keys of all known crops (used for the suggestions list in the Crops form).
export function knownCropKeys() {
    return Object.keys(enCrops.names)
}