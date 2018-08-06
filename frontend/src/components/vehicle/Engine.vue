<template>
  <v-container fluid>
    <v-layout align-center
              justify-center
              class="mt-2 notSelectable">
      <v-flex xs12>

        <v-card>

          <v-card-title>
            <div class="title">Elementy silnika:</div>
            <v-spacer />
            <v-text-field v-model="search"
                          append-icon="search"
                          label="Search"
                          single-line
                          hide-details >
            </v-text-field>
          </v-card-title>

          <v-data-table
            :headers="headers"
            :items="desserts"
            :search="search"
            v-model="selected"
            select-all
            rows-per-page-text="Wierszy na stronę"
            item-key="name"
            class="elevation-1">

            <template slot="items" slot-scope="props">
              <td>
                <v-checkbox v-model="props.selected"
                            primary
                            hide-details />
              </td>
              <td class="hover-cell" @click="props.expanded = !props.expanded">{{ props.item.name }}</td>
              <td class="hover-cell" @click="props.expanded = !props.expanded">{{ props.item.calories }}</td>
              <td class="hover-cell" @click="props.expanded = !props.expanded">{{ props.item.fat }}</td>
              <td class="hover-cell" @click="props.expanded = !props.expanded">{{ props.item.carbs }}</td>
              <td class="hover-cell" @click="props.expanded = !props.expanded">{{ props.item.protein }}</td>
              <td class="hover-cell" @click="props.expanded = !props.expanded">{{ props.item.iron }}</td>
            </template>

            <template slot="expand" slot-scope="props">
              <v-container>
                <v-layout>
                  <v-flex>TEST</v-flex>
                </v-layout>
              </v-container>
            </template>

            <v-alert slot="no-results" :value="true" color="error" icon="warning">
              Brak rezultatów dla "{{ search }}"...
            </v-alert>

            <template slot="headerCell" slot-scope="props">
              <v-tooltip bottom>
                <span slot="activator" id="header-inside-tooltip-activator-slot">
                  {{ props.header.text }}
                </span>
                  <span id="tooltip">
                  {{ props.header.text }}
                </span>
              </v-tooltip>
            </template>

            <template slot="footer">
              <td colspan="100%">
                <em>* Kliknij w daną komórkę aby rozwinąć detale</em>
              </td>
            </template>

          </v-data-table>

        </v-card>

      </v-flex>
    </v-layout>

  </v-container>
</template>

<script>
  export default {
    data() {
      return {
        max25chars: (v) => v.length <= 25 || 'Input too long!',
        search: '',
        headers: [
          {
            text: 'Dessert (100g serving)',
            align: 'left',
            sortable: false,
            value: 'name'
          },
          {
            text: 'Calories',
            value: 'calories'
          },
          {
            text: 'Fat (g)',
            value: 'fat'
          },
          {
            text: 'Carbs (g)',
            value: 'carbs'
          },
          {
            text: 'Protein (g)',
            value: 'protein'
          },
          {
            text: 'Iron (%)',
            value: 'iron'
          }
        ],
        selected: [],
        desserts: [
          {
            value: false,
            name: 'Frozen Yogurt',
            calories: 159,
            fat: 6.0,
            carbs: 24,
            protein: 4.0,
            iron: '1%'
          },
          {
            value: false,
            name: 'Ice cream sandwich',
            calories: 237,
            fat: 9.0,
            carbs: 37,
            protein: 4.3,
            iron: '1%'
          },
          {
            value: false,
            name: 'Eclair',
            calories: 262,
            fat: 16.0,
            carbs: 23,
            protein: 6.0,
            iron: '7%'
          },
          {
            value: false,
            name: 'Cupcake',
            calories: 305,
            fat: 3.7,
            carbs: 67,
            protein: 4.3,
            iron: '8%'
          },
          {
            value: false,
            name: 'Gingerbread',
            calories: 356,
            fat: 16.0,
            carbs: 49,
            protein: 3.9,
            iron: '16%'
          },
          {
            value: false,
            name: 'Jelly bean',
            calories: 375,
            fat: 0.0,
            carbs: 94,
            protein: 0.0,
            iron: '0%'
          },
          {
            value: false,
            name: 'Lollipop',
            calories: 392,
            fat: 0.2,
            carbs: 98,
            protein: 0,
            iron: '2%'
          },
          {
            value: false,
            name: 'Honeycomb',
            calories: 408,
            fat: 3.2,
            carbs: 87,
            protein: 6.5,
            iron: '45%'
          },
          {
            value: false,
            name: 'Donut',
            calories: 452,
            fat: 25.0,
            carbs: 51,
            protein: 4.9,
            iron: '22%'
          },
          {
            value: false,
            name: 'KitKat',
            calories: 518,
            fat: 26.0,
            carbs: 65,
            protein: 7,
            iron: '6%'
          }
        ]
      }
    }
  }
</script>

<style scoped>
  .hover-cell:hover {
    cursor: pointer;
    color: #0097A7;
  }
</style>
