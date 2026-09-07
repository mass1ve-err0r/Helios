import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'

import { createVuetify } from 'vuetify'


const heliosTheme = {
    dark: false,

    colors: {
        /*
         * Core application surfaces
         */
        background: '#F7FBFE',
        surface: '#FFFFFF',
        'surface-bright': '#F1F6FA',
        'surface-light': '#EDF3F7',
        'surface-variant': '#DCE6ED',

        primary: '#2E86DE',
        'primary-darken-1': '#246FB9',
        'primary-lighten-1': '#569FFF',

        secondary: '#00A3A4',
        'secondary-darken-1': '#007F80',
        'secondary-lighten-1': '#00D2D3',

        /*
         * Main foreground colors
         */
        foreground: '#212F3E',
        muted: '#576574',
        border: '#D8E2E9',

        /*
         * Semantic colors using FlatUI 2
         */
        success: '#10AC84',
        info: '#54A0FF',
        warning: '#FF9F43',
        error: '#EE5353',

        /*
         * FlatUI 2 auxiliary palette
         */
        flatPink: '#F368E0',
        flatLightPink: '#FF9FF3',

        flatYellow: '#FECA57',
        flatOrange: '#FF9F43',
        flatCoral: '#FF6B6B',
        flatRed: '#EE5353',

        flatTeal: '#4DDBFB',
        flatBlue: '#569FFF',
        flatLightBlue: '#54A0FF',
        flatDarkBlue: '#2E86DE',

        flatAqua: '#00D2D3',
        flatForest: '#00A3A4',

        flatGreen: '#1CD1A1',
        flatDarkGreen: '#10AC84',

        flatPurple: '#5F27CD',
        flatDarkPurple: '#341E97',

        flatLightGrey: '#C8D6E5',
        flatMediumGrey: '#8395A7',
        flatDarkGrey: '#576574',
        flatBlack: '#212F3E'
    },

    variables: {
        'border-color': '#D8E2E9',
        'border-opacity': 1,

        'high-emphasis-opacity': 1,
        'medium-emphasis-opacity': 0.72,
        'disabled-opacity': 0.42,

        'idle-opacity': 0.05,
        'hover-opacity': 0.06,
        'focus-opacity': 0.10,
        'selected-opacity': 0.12,
        'activated-opacity': 0.16,
        'pressed-opacity': 0.18,
        'dragged-opacity': 0.14
    }
}


export default createVuetify({
    icons: {
        defaultSet: 'mdi'
    },

    theme: {
        defaultTheme: 'heliosTheme',

        themes: {
            heliosTheme
        }
    },

    defaults: {
        VApp: {
            theme: 'heliosTheme'
        },

        VAppBar: {
            color: 'surface',
            elevation: 0
        },

        VNavigationDrawer: {
            color: 'surface',
            elevation: 0
        },

        VCard: {
            color: 'surface',
            elevation: 0,
            rounded: 'lg'
        },

        VBtn: {
            color: 'primary',
            rounded: 'lg',
            elevation: 0
        },

        VTextField: {
            color: 'primary',
            variant: 'outlined'
        },

        VSelect: {
            color: 'primary',
            variant: 'outlined'
        },

        VTextarea: {
            color: 'primary',
            variant: 'outlined'
        },

        VCheckbox: {
            color: 'primary'
        },

        VSwitch: {
            color: 'primary'
        },

        VProgressCircular: {
            color: 'primary'
        }
    }
})
