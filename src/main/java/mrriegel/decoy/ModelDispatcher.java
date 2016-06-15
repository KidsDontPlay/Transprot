package mrriegel.decoy;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelDispatcher - riegel
 * Created using Tabula 4.1.1
 */
public class ModelDispatcher extends ModelBase {
    public ModelRenderer layer1;
    public ModelRenderer layer2;
    public ModelRenderer layer3;
    public ModelRenderer pile1;
    public ModelRenderer pile2;
    public ModelRenderer pile3;
    public ModelRenderer pile4;

    public ModelDispatcher() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.layer3 = new ModelRenderer(this, 0, 28);
        this.layer3.setRotationPoint(-3.5F, 4.0F, -3.5F);
        this.layer3.addBox(0.0F, 0.0F, 0.0F, 7, 1, 7, 0.0F);
        this.layer2 = new ModelRenderer(this, 0, 16);
        this.layer2.setRotationPoint(-5.5F, 5.0F, -5.5F);
        this.layer2.addBox(0.0F, 0.0F, 0.0F, 11, 1, 11, 0.0F);
        this.pile2 = new ModelRenderer(this, 0, 0);
        this.pile2.setRotationPoint(1.0F, 0.0F, -2.0F);
        this.pile2.addBox(0.0F, 0.0F, 0.0F, 1, 4, 1, 0.0F);
        this.pile3 = new ModelRenderer(this, 0, 0);
        this.pile3.setRotationPoint(-2.0F, 0.0F, 1.0F);
        this.pile3.addBox(0.0F, 0.0F, 0.0F, 1, 4, 1, 0.0F);
        this.layer1 = new ModelRenderer(this, 0, 0);
        this.layer1.setRotationPoint(-7.0F, 6.0F, -7.0F);
        this.layer1.addBox(0.0F, 0.0F, 0.0F, 14, 2, 14, 0.0F);
        this.pile1 = new ModelRenderer(this, 0, 0);
        this.pile1.setRotationPoint(1.0F, 0.0F, 1.0F);
        this.pile1.addBox(0.0F, 0.0F, 0.0F, 1, 4, 1, 0.0F);
        this.pile4 = new ModelRenderer(this, 0, 0);
        this.pile4.setRotationPoint(-2.0F, 0.0F, -2.0F);
        this.pile4.addBox(0.0F, 0.0F, 0.0F, 1, 4, 1, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.layer3.render(f5);
        this.layer2.render(f5);
        this.pile2.render(f5);
        this.pile3.render(f5);
        this.layer1.render(f5);
        this.pile1.render(f5);
        this.pile4.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}