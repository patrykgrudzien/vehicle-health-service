import Vue from 'vue'
import VueI18n from 'vue-i18n'
import en from './en'
import pl from './pl'

Vue.use(VueI18n)

const locale = 'pl'
const messages = { en, pl }

const i18n = new VueI18n({
  locale,
  messages
})

export default i18n
