import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { CategoryComponent } from './list-category/category.component';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CoreDirectivesModule } from '@core/directives/directives';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgSelectModule } from '@ng-select/ng-select';
import { TranslateModule } from '@ngx-translate/core';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { AddCategoryComponent } from './add-category/add-category.component';
import { DetailCategoryComponent } from './detail-category/detail-category.component';
import { EditCategoryComponent } from './edit-category/edit-category.component';

const routes: Routes = [
  {
    path: 'list',
    component: CategoryComponent,
    data: { animation: 'feather' }
  }
];

@NgModule({
  declarations: [
    CategoryComponent,
    AddCategoryComponent,
    DetailCategoryComponent,
    EditCategoryComponent
  ],
  imports: [
    CommonModule,
    NgbModule,
    CoreDirectivesModule,
    NgxDatatableModule,
    CorePipesModule,
    ReactiveFormsModule,
    FormsModule,
    NgSelectModule,
    ContentHeaderModule,
    RouterModule.forChild(routes),
    TranslateModule
  ]
})
export class CategoryModule { }
